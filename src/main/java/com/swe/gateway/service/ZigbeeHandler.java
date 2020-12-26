package com.swe.gateway.service;


import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.dao.SensorObsPropMapper;
import com.swe.gateway.model.Observation;
import com.swe.gateway.model.Sensor;
import com.swe.gateway.model.StructObservation;
import com.swe.gateway.model.ZigBeeData;
import com.swe.gateway.util.CRCUtil;
import com.swe.gateway.util.ConvertUtil;
import com.swe.gateway.util.SOSWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Mono 和 Flux 适用于两个场景，即：
 * Mono：实现发布者，并返回 0 或 1 个元素，即单对象。
 * Flux：实现发布者，并返回 N 个元素，即 List 列表对象。
 * 有人会问，这为啥不直接返回对象，比如返回 City/Long/List。
 * 原因是，直接使用 Flux 和 Mono 是非阻塞写法，相当于回调方式。
 * 利用函数式可以减少了回调，因此会看不到相关接口。这恰恰是 WebFlux 的好处：集合了非阻塞 + 异步
 */

@Service
public class ZigbeeHandler {

    private static Logger logger = LogManager.getLogger(ZigbeeHandler.class.getName());
    private static Map<String, Boolean> channels = new ConcurrentHashMap<>();


    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationMapper observationMapper;
    @Autowired
    SensorObsPropMapper sensorObsPropMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    public class DecoderHandler extends MessageToMessageDecoder<ByteBuf> {
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List list) throws Exception {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String str = "";
            for (int i = 0; i < bytes.length; i++) {
                str += bytes[i] + " ";
            }
            logger.info("收到消息：" + str);
            list.add(bytes);
            Thread.sleep(5000);
        }
    }

    public class EncoderHandler extends MessageToMessageEncoder<byte[]> {

        @Override
        protected void encode(ChannelHandlerContext ctx, byte[] o, List list) throws Exception {
            list.add(o);
            ByteBuf buf = ctx.alloc().buffer(o.length);
            buf.writeBytes(o);
            list.add(buf);
            String str = "";
            for (int i = 0; i < o.length; i++) {
                str += o[i] + " ";
            }
            logger.info("发送消息：" + str);
        }

    }

    public class TcpHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //得到消息后，可根据消息类型分发给不同的service去处理数据
            byte[] preHandlerAfferentMsg = (byte[]) msg;
            String str = "";
            for (int i = 0; i < preHandlerAfferentMsg.length; i++) {
                str += preHandlerAfferentMsg[i] + " ";
            }
            byte[] data = null;
            String key = ctx.channel().id().asLongText();
            if (channels.containsKey(key)) {
                if (channels.get(key)) {
                    getZigBeeData(preHandlerAfferentMsg);
                    data = createIndication(true);
                } else {
                    getLoraData(preHandlerAfferentMsg);
                    data = createIndication(false);

                }
            } else {
                if (preHandlerAfferentMsg[0] == 119) { // 表示建立连接的是Lora网关
                    //getLoraData ();
                    data = createIndication(false);
                    channels.put(key, false);
                } else if (preHandlerAfferentMsg[0] == 17) {

                    data = createIndication(true);
                    channels.put(key, true);
                }
            }
            if (data != null) {
                ctx.writeAndFlush(data); //返回数据给tcP Client
            }
            logger.info("channelRead");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.info("exceptionCaught");
            ctx.fireExceptionCaught(cause);
        }
    }


    //测试数据：{"id":1,"provinceId":2,"cityName":"111","description":{text:"test"}}
    public Mono<ServerResponse> parseAndSendZigbeeData(ServerRequest request) {
        Mono<String> str = request.bodyToMono(String.class);
        return str.flatMap(s -> {
            logger.info(s);
            JSONObject json = JSONObject.parseObject(s);//能够解析整个json串
            String[] datas = json.getString("data").split(" ");
            byte[] r_buffer = new byte[datas.length];
            for (int i = 0; i < datas.length; i++)
                r_buffer[i] = Byte.parseByte(datas[i]);
            getZigBeeData(r_buffer);
            return ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(s), String.class);
        });
    }

    public void getZigBeeData(byte[] r_buffer) {
        String originData = "";
        for (int i = 0; i < r_buffer.length; i++) {
            originData += r_buffer[i]+" ";
        }
        //把zigbee的Modbus数据发送给前端
        Observation obsModbus = new Observation();
        obsModbus.setObsValue(originData);
        RealTimeHandler.REALTIME_DATA.put("ModBus-001_网关数据", obsModbus);

        if (r_buffer.length < 34)
            return;
        List<SOSWrapper> sosWrappers = new ArrayList<SOSWrapper>();//传感器SOS封装类对象列表
        List<StructObservation> lstStructObs01;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs02;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs03;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs04;//传感器观测信息结构体列表
        String _slaveAddress = Byte.toString(r_buffer[0]);//从机地址
        String _command = Byte.toString(r_buffer[1]);//操作码
        String _numBytes = Byte.toString(r_buffer[2]);//字节数
        String logText = "接收数据通过CRC检校，数据正确！" +
                " 从机地址:" + _slaveAddress +
                " 功能码:" + _command +
                " 数据字节数:" + _numBytes;
        //写日志
        logger.info(logText);

        logText = "实时数据：";

        //获取气象要素数据
        //region 风速
        double dwendu = ConvertUtil.getShort(r_buffer, 19) * 0.1;//温度，精度为0.1℃
        double dshidu = ConvertUtil.getShort(r_buffer, 21) * 0.1;//湿度，精度为0.1% RH
        double dyuliang = ConvertUtil.getShort(r_buffer, 23) * 0.1;//雨量，精度为1mm/24h
        double dfengsu = ConvertUtil.getShort(r_buffer, 25) * 0.1;//风速，精度为0.1m/s
        double dfengxiang = ConvertUtil.getShort(r_buffer, 27);//风向，精度为1°
        //double dbeiyong = ConvertUtil.getShort(r_buffer, 3) * 0.1;//精度为？，备用
        double ddianya = ConvertUtil.getShort(r_buffer, 31) * 0.1;//电压，精度为0.1V
        double dzhouqi = ConvertUtil.getShort(r_buffer, 33);//周期以秒为单位
        double dpm = ConvertUtil.getShort(r_buffer, 7);          //pm2.5，单位是毫克每立方米
        double dtvoc = ConvertUtil.getShort(r_buffer, 13) * 0.01;   // 总挥发性有机物，单位ppm 百万分比浓度

        ArrayList<ZigBeeData> zigbeeDataSensor1 = new ArrayList<>();
        ArrayList<ZigBeeData> zigbeeDataSensor2 = new ArrayList<>();
        zigbeeDataSensor1.add(0, new ZigBeeData(5, dpm, "PM2.5"));
        zigbeeDataSensor1.add(1, new ZigBeeData(6, dwendu, "环境温度"));
        zigbeeDataSensor1.add(2, new ZigBeeData(7, dshidu, "环境湿度"));
        zigbeeDataSensor1.add(3, new ZigBeeData(13, dtvoc, "总挥发性有机物"));

        zigbeeDataSensor2.add(0, new ZigBeeData(8, dyuliang, "雨量"));
        zigbeeDataSensor2.add(1, new ZigBeeData(9, dfengsu, "风速"));
        zigbeeDataSensor2.add(2, new ZigBeeData(10, dfengxiang, "风向"));

        Sensor sensor1 = sensorMapper.getSensorByName("ZigBee-" + "001");
        Sensor sensor2 = sensorMapper.getSensorByName("ZigBee-" + "002");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        Integer day = Integer.valueOf(df.format(date));
        for (int i = 0; i < zigbeeDataSensor1.size(); i++) {
            Observation obs1 = new Observation();
            ZigBeeData zigBeeData = zigbeeDataSensor1.get(i);
            obs1.setSensorId(sensor1.getSensorId());
            obs1.setObsPropId(zigBeeData.getObsId());
            obs1.setDay(day);
            obs1.setHour(date.getHours());
            obs1.setTimestamp(date);
            obs1.setObsValue(Double.toString(zigBeeData.getObs()));
            observationMapper.insert(obs1);
            RealTimeHandler.REALTIME_DATA.put(sensor1.getSensorName() + "_" + zigBeeData.getObsName(), obs1);
            logger.info(obs1);
        }
        for (int i = 0; i < zigbeeDataSensor2.size(); i++) {
            Observation obs1 = new Observation();
            ZigBeeData zigBeeData = zigbeeDataSensor2.get(i);
            obs1.setSensorId(sensor2.getSensorId());
            obs1.setObsPropId(zigBeeData.getObsId());
            obs1.setDay(day);
            obs1.setHour(date.getHours());
            obs1.setTimestamp(date);
            obs1.setObsValue(Double.toString(zigBeeData.getObs()));
            observationMapper.insert(obs1);
            RealTimeHandler.REALTIME_DATA.put(sensor2.getSensorName() + "_" + zigBeeData.getObsName(), obs1);
            logger.info(obs1);
        }

        logText += "温度：" + dwendu + "℃，湿度：" + dshidu + "%RH，pm2.5：" + dpm + "mg/m3,雨量：" + dyuliang + "mm/24h,风速：" + dfengsu + "m/s,风向：" + dfengxiang + "°，TVOC总挥发性有机物：" + dtvoc + "ppm,电压：" + ddianya + "V,周期：" + dzhouqi + "s";
        logger.info(logText);
    }

    public void getLoraData(byte[] r_buffer) {
        String originData = "";
        for (int i = 0; i < r_buffer.length; i++) {
            originData += r_buffer[i]+" ";
        }
        //把Lora的Modbus数据发送给前端
        Observation obsModbus = new Observation();
        obsModbus.setObsValue(originData);
        RealTimeHandler.REALTIME_DATA.put("ModBus-002_网关数据", obsModbus);
        if (r_buffer.length < 16)
            return;
        List<SOSWrapper> sosWrappers = new ArrayList<SOSWrapper>();//传感器SOS封装类对象列表
        List<StructObservation> lstStructObs01;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs02;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs03;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs04;//传感器观测信息结构体列表
        String _slaveAddress = Byte.toString(r_buffer[0]);//从机地址
        String _command = Byte.toString(r_buffer[1]);//操作码
        String _numBytes = Byte.toString(r_buffer[2]);//字节数
        String logText = "接收数据通过CRC检校，数据正确！" +
                " 从机地址:" + _slaveAddress +
                " 功能码:" + _command +
                " 数据字节数:" + _numBytes;
        //写日志
        logger.info(logText);

        logText = "实时数据：";

        double ph = ConvertUtil.getShort(r_buffer, 11) * 0.2;//ph
        double v1 = ConvertUtil.getShort(r_buffer, 15) * 0.1;//电压
        double v2 = ConvertUtil.getShort(r_buffer, 17);//周期
        double v3 = ConvertUtil.getShort(r_buffer, 19);//ph

        ArrayList<ZigBeeData> loraDataSensor = new ArrayList<>();
        loraDataSensor.add(0, new ZigBeeData(18, ph, "土壤PH"));
        Sensor sensor = sensorMapper.getSensorByName("Lora-" + "001");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        Integer day = Integer.valueOf(df.format(date));
        for (int i = 0; i < loraDataSensor.size(); i++) {
            Observation obs1 = new Observation();
            obs1.setSensorId(sensor.getSensorId());
            ZigBeeData loraData = loraDataSensor.get(i);
            obs1.setObsPropId(loraData.getObsId());
            obs1.setDay(day);
            obs1.setHour(date.getHours());
            obs1.setTimestamp(date);
            obs1.setObsValue(Double.toString(loraData.getObs()));
            observationMapper.insert(obs1);
            RealTimeHandler.REALTIME_DATA.put(sensor.getSensorName() + "_" + loraData.getObsName(), obs1);
            logger.info(obs1);
        }
    }

    ;

    private byte[] createIndication(Boolean isZigbee) {
        byte[] sendData = null;
        if (isZigbee) {
            sendData = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x10, (byte) 0x00, (byte) 0x00};
        } else
            sendData = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x08, (byte) 0x00, (byte) 0x00};
        return CRCUtil.CRCCalc(sendData);//计算CRC（循环冗余检测）代码
    }
}


