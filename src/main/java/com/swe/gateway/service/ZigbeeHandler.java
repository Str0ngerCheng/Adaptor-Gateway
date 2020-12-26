package com.swe.gateway.service;


import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.config.SOSConfig;
import com.swe.gateway.config.ZigbeeConfig;
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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

    private static Logger logger = LogManager.getLogger (ZigbeeHandler.class.getName ( ));
    private static Map<String, Boolean> channels = new ConcurrentHashMap<> ( );

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
            Thread.sleep(5000);
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
                if (preHandlerAfferentMsg[0] == 119) { // 表示建立连接的是Lora网关
                    //getLoraData ();
                    data = createIndication (false);
                    channels.put (key, false);
                } else  if (preHandlerAfferentMsg[0] == 17)  {

                    data = createIndication (true);
                    channels.put (key, true);
                }
            }
            if(data!=null) {
                ctx.writeAndFlush(data); //返回数据给tcP Client
            }
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
        if(r_buffer.length<34)
            return ;
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
        double dwendu = ConvertUtil.getShort (r_buffer, 19) * 0.1;//温度，精度为0.1℃
        double dshidu = ConvertUtil.getShort (r_buffer, 21) * 0.1;//湿度，精度为0.1% RH
        double dyuliang = ConvertUtil.getShort (r_buffer, 23) * 0.1;//雨量，精度为1mm/24h
        double dfengsu = ConvertUtil.getShort (r_buffer, 25) * 0.1;//风速，精度为0.1m/s
        double dfengxiang = ConvertUtil.getShort (r_buffer, 27);//风向，精度为1°
        //double dbeiyong = ConvertUtil.getShort(r_buffer, 3) * 0.1;//精度为？，备用
        double ddianya = ConvertUtil.getShort (r_buffer, 31) * 0.1;//电压，精度为0.1V
        double dzhouqi = ConvertUtil.getShort (r_buffer, 33);//周期以秒为单位
        double dpm = ConvertUtil.getShort (r_buffer, 7);          //pm2.5，单位是毫克每立方米
        double dtvoc = ConvertUtil.getShort (r_buffer, 13) * 0.01;   // 总挥发性有机物，单位ppm 百万分比浓度
        logText += "温度：" + dwendu + "℃，湿度：" + dshidu + "%RH，pm2.5：" + dpm + "mg/m3,雨量：" + dyuliang + "mm/24h,风速：" + dfengsu + "m/s,风向：" + dfengxiang + "°，TVOC总挥发性有机物：" + dtvoc + "ppm,电压：" + ddianya + "V,周期：" + dzhouqi + "s";
        logger.info (logText);
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


