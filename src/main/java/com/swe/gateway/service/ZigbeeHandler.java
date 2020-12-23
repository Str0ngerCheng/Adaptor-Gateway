package com.swe.gateway.service;


import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.config.SOSConfig;
import com.swe.gateway.config.ZigbeeConfig;
import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.dao.SensorObsPropMapper;
import com.swe.gateway.model.Observation;
import com.swe.gateway.model.ObservationProperty;
import com.swe.gateway.model.Sensor;
import com.swe.gateway.model.StructObservation;
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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
public class ZigbeeHandler  {

    private static Logger logger = LogManager.getLogger (ZigbeeHandler.class.getName ( ));
    private static Map<String, Boolean> channels = new ConcurrentHashMap<> ( );
    private static ConcurrentHashMap<Integer,Double> zigbeeData=new ConcurrentHashMap<>();
    private Map<String, Observation> zigbeeDataMap = RealTimeHandler.REALTIME_DATA;

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
            byte[] bytes = new byte[buf.readableBytes ( )];
            buf.readBytes (bytes);
            String str = "";
            for (int i = 0; i < bytes.length; i++) {
                str += bytes[i] + " ";
            }
            logger.info ("收到消息：" + str);
            list.add (bytes);
        }
    }

    public class EncoderHandler extends MessageToMessageEncoder<byte[]> {

        @Override
        protected void encode(ChannelHandlerContext ctx, byte[] o, List list) throws Exception {
            list.add (o);
            ByteBuf buf = ctx.alloc ( ).buffer (o.length);
            buf.writeBytes (o);
            list.add (buf);
            String str = "";
            for (int i = 0; i < o.length; i++) {
                str += o[i] + " ";
            }
            logger.info ("发送消息：" + str);
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
            String key = ctx.channel ( ).id ( ).asLongText ( );
            if (channels.containsKey (key)) {
                if (channels.get (key)) {
                    getZigBeeData(preHandlerAfferentMsg);
                    data = createIndication (true);
                } else {
                    getLoraData (preHandlerAfferentMsg);
                    data = createIndication (false);

                }
            } else {
                if (preHandlerAfferentMsg[0] == 17) { // 表示建立连接的是Lora网关
                    //getLoraData ();
                    data = createIndication (false);
                    channels.put (key, false);
                } else {
                    data = createIndication (true);
                    channels.put (key, true);
                }
            }
            ctx.writeAndFlush (data); //返回数据给tcP Client
            logger.info ("channelRead");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.info ("exceptionCaught");
            ctx.fireExceptionCaught (cause);
        }
    }


    //测试数据：{"id":1,"provinceId":2,"cityName":"111","description":{text:"test"}}
    public Mono<ServerResponse> parseAndSendZigbeeData(ServerRequest request) {
        Mono<String> str = request.bodyToMono (String.class);
        return str.flatMap (s -> {
            logger.info (s);
            JSONObject json = JSONObject.parseObject (s);//能够解析整个json串
            String[] datas = json.getString ("data").split (" ");
            byte[] r_buffer = new byte[datas.length];
            for (int i = 0; i < datas.length; i++)
                r_buffer[i] = Byte.parseByte (datas[i]);
            getZigBeeData(r_buffer);
            return ServerResponse.ok ( ).contentType (APPLICATION_JSON).body (Mono.just (s), String.class);
        });
    }

    public void getZigBeeData(byte[] r_buffer) {
        List<SOSWrapper> sosWrappers = new ArrayList<SOSWrapper> ( );//传感器SOS封装类对象列表
        List<StructObservation> lstStructObs01;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs02;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs03;//传感器观测信息结构体列表
        List<StructObservation> lstStructObs04;//传感器观测信息结构体列表
        String _slaveAddress = Byte.toString (r_buffer[0]);//从机地址
        String _command = Byte.toString (r_buffer[1]);//操作码
        String _numBytes = Byte.toString (r_buffer[2]);//字节数
        String logText = "接收数据通过CRC检校，数据正确！" +
                " 从机地址:" + _slaveAddress +
                " 功能码:" + _command +
                " 数据字节数:" + _numBytes;
        //写日志
        logger.info (logText);

        logText = "实时数据：";

        //获取气象要素数据
        //region 风速
        double dwendu = ConvertUtil.getShort (r_buffer, 19) * 0.1;//环境温度，精度为0.1℃
        double dshidu = ConvertUtil.getShort (r_buffer, 21) * 0.1;//环境湿度，精度为0.1% RH
        double dyuliang = ConvertUtil.getShort (r_buffer, 23) * 0.1;//雨量，精度为1mm/24h
        double dfengsu = ConvertUtil.getShort (r_buffer, 25) * 0.1;//风速，精度为0.1m/s
        double dfengxiang = ConvertUtil.getShort (r_buffer, 27);//风向，精度为1°
        //double dbeiyong = ConvertUtil.getShort(r_buffer, 3) * 0.1;//精度为？，备用
        double ddianya = ConvertUtil.getShort (r_buffer, 31) * 0.1;//电压，精度为0.1V
        double dzhouqi = ConvertUtil.getShort (r_buffer, 33);//周期以秒为单位
        double dpm = ConvertUtil.getShort (r_buffer, 7);          //pm2.5，单位是毫克每立方米
        double dtvoc = ConvertUtil.getShort (r_buffer, 13) * 0.01;   // 总挥发性有机物，单位ppm 百万分比浓度

        zigbeeData.put(6,dwendu);
        zigbeeData.put(7,dshidu);
        zigbeeData.put(8,dyuliang);
        zigbeeData.put(9,dfengsu);
        zigbeeData.put(10,dfengxiang);
        zigbeeData.put(11,ddianya);
        zigbeeData.put(12,dzhouqi);
        zigbeeData.put(5,dpm);
        zigbeeData.put(13,dtvoc);

        Sensor sensor=sensorMapper.getSensorByName ("ZigBee-" + "001");
        SimpleDateFormat df = new SimpleDateFormat ("yyyyMMdd");
        Date date = new Date ( );
        Integer day = Integer.valueOf (df.format (date));
        for(int i=0;i<9;i++) {
            Observation obs1 = new Observation();
            obs1.setSensorId(sensor.getSensorId());
            obs1.setObsPropId(5 + i);
            obs1.setDay(day);
            obs1.setHour(date.getHours());
            obs1.setTimestamp(date);
            obs1.setObsValue(Double.toString(zigbeeData.get(5 + i)));
            observationMapper.insert(obs1);
            ObservationProperty observationProperty=observationPropertyMapper.getObsPropById(5+i);
            zigbeeDataMap.put(sensor.getSensorName()+"_"+observationProperty.getObsPropName(),obs1);
            logger.info(obs1);
        }

        logText += "温度：" + dwendu + "℃，湿度：" + dshidu + "%RH，pm2.5：" + dpm + "mg/m3,雨量：" + dyuliang + "mm/24h,风速：" + dfengsu + "m/s,风向：" + dfengxiang + "°，TVOC总挥发性有机物：" + dtvoc + "ppm,电压：" + ddianya + "V,周期：" + dzhouqi + "s";
        logger.info (logText);


        //从上到下依次为各个观测值的结构体
        StructObservation _structObsTemperature = new StructObservation (SOSConfig.Temperature_ObsProperty, SOSConfig.Temperature_ObsResultName, SOSConfig.Temperature_ObsResultUom, dwendu);
        StructObservation _structObsHumidity = new StructObservation (SOSConfig.Humidity_ObsProperty, SOSConfig.Humidity_ObsResultName, SOSConfig.Humidity_ObsResultUom, dshidu);
        StructObservation _structObspm = new StructObservation (SOSConfig.PM_ObsProperty, SOSConfig.PM_ObsResultName, SOSConfig.PM_ObsResultUom, dpm);
        StructObservation _structObsRainFall = new StructObservation (SOSConfig.RainFall_ObsProperty, SOSConfig.RainFall_ObsResultName, SOSConfig.RainFall_ObsResultUom, dyuliang);
        StructObservation _structObsWindSpeed = new StructObservation (SOSConfig.WindSpeed_ObsProperty, SOSConfig.WindSpeed_ObsResultName, SOSConfig.WindSpeed_ObsResultUom, dfengsu);
        StructObservation _structObsWindDirection = new StructObservation (SOSConfig.WindDirection_ObsProperty, SOSConfig.WindDirection_ObsResultName, SOSConfig.WindDirection_ObsResultUom, dfengxiang);
        StructObservation _structObsTVOC = new StructObservation (SOSConfig.TVOC_ObsProperty, SOSConfig.TVOC_ObsResultName, SOSConfig.TVOC_ObsResultUom, dtvoc);
        lstStructObs01 = new ArrayList<> ( );
        //将各个观测值结构体加入观测值信息结构体列表中
        lstStructObs01.add (_structObsTemperature);
        lstStructObs01.add (_structObsHumidity);
        lstStructObs01.add (_structObspm);
        lstStructObs01.add (_structObsTVOC);
        lstStructObs02 = new ArrayList<> ( );
        lstStructObs02.add (_structObsRainFall);
        lstStructObs03 = new ArrayList<> ( );
        lstStructObs03.add (_structObsWindSpeed);

        lstStructObs04 = new ArrayList<> ( );
        lstStructObs04.add (_structObsWindDirection);
        String samplingTime = new DateTime ( ).toString ( );//观测时间

        SOSWrapper modbus01_SOSWrapper = new SOSWrapper (ZigbeeConfig.SensorID_Node001_Modbus_01, samplingTime, ZigbeeConfig.Longitude, ZigbeeConfig.Latitude, lstStructObs01, SOSConfig.SOS_Url);
        SOSWrapper modbus02_SOSWrapper = new SOSWrapper (ZigbeeConfig.SensorID_Node001_Modbus_02, samplingTime, ZigbeeConfig.Longitude, ZigbeeConfig.Latitude, lstStructObs02, SOSConfig.SOS_Url);
        SOSWrapper modbus03_SOSWrapper = new SOSWrapper (ZigbeeConfig.SensorID_Node001_Modbus_03, samplingTime, ZigbeeConfig.Longitude, ZigbeeConfig.Latitude, lstStructObs03, SOSConfig.SOS_Url);
        SOSWrapper modbus04_SOSWrapper = new SOSWrapper (ZigbeeConfig.SensorID_Node001_Modbus_04, samplingTime, ZigbeeConfig.Longitude, ZigbeeConfig.Latitude, lstStructObs04, SOSConfig.SOS_Url);
        sosWrappers.add (modbus01_SOSWrapper);
        sosWrappers.add (modbus02_SOSWrapper);
        sosWrappers.add (modbus03_SOSWrapper);
        sosWrappers.add (modbus04_SOSWrapper);
        //#endregion
        if (sosWrappers != null) {
            for (SOSWrapper sosWrapper : sosWrappers) {
                if (sosWrapper != null) {
                    sosWrapper.insertSOS ( );
                }
            }
        }
    }

    public void getLoraData(byte[] r_buffer){};
    private byte[] createIndication(Boolean isZigbee) {
        byte[] sendData = null;
        if (isZigbee) {
            sendData = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x10, (byte) 0x00, (byte) 0x00};
        } else
            sendData = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x08, (byte) 0x00, (byte) 0x00};
        return CRCUtil.CRCCalc (sendData);//计算CRC（循环冗余检测）代码
    }
}


