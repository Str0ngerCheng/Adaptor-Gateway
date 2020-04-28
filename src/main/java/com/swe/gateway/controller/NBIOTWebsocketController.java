package com.swe.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.model.Observation;
import com.swe.gateway.util.ServerEncoder;
import com.swe.gateway.util.WebSocketUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/websocket/{usernick}", encoders = {ServerEncoder.class})
public class NBIOTWebsocketController {
    //用了一个Map存储传感器的实时数据，RRALTIME_DATA只需要存每个设备的每个观测属性的最新一条数据就可以了
    //key为sensorId+'_'+obsPropId组成的字符串
    //前端点击发送一个传感器的设备id和一个观测属性id，然后去获取传感器的最新一条数据
    //可以用http://coolaf.com/tool/chattest测试ws的接口
    private static final ConcurrentHashMap<String, Observation> RRALTIME_DATA = new ConcurrentHashMap<>();
    //这个变量去判断是否继续向当前session持续发送消息
    //不用interrupt()是因为在调用isinterrupted()判断之后标志位又会被置为true，另外这样可以控制向多个客户端的发送情况
    private static final ConcurrentHashMap<String, Integer> SESSION_SIGNAL = new ConcurrentHashMap<>();
    private static final  Logger logger = LogManager.getLogger(NBIOTWebsocketController.class.getName());
    @OnOpen
    public void onOpen(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "客户端[" + userNick + "]建立连接!";
        logger.info(message);
        WebSocketUtil.addSession(userNick, session);
//      模拟RRALTIME_DATA里面的数据，理论上应该是传感器端传入数据，在插入数据库的同时存一份到RRALTIME_DATA里面
        Observation obs=new Observation(1,1,6,4,"24.5",new Date(System.currentTimeMillis()));
        String sensorId_obsPropId=obs.getSensorId()+"_"+obs.getObsPropId();
        RRALTIME_DATA.put(sensorId_obsPropId,obs);
        Observation obs1=new Observation(1,2,6,4,"8.5",new Date(System.currentTimeMillis()));
        sensorId_obsPropId=obs1.getSensorId()+"_"+obs1.getObsPropId();
        RRALTIME_DATA.put(sensorId_obsPropId,obs1);
//        模拟实时数据
//        Thread nbiotDataProducer = new Thread(() -> {
//            while (true) {
//                double temperature = (Math.random() * 1000) % 30;
//                double humidity = (Math.random() * 1000) % 60;
//                String diveceid = "NBIOT-NODE-" + (int) (Math.random() * 1000) % 10;
//                NBIOT nbiot = new NBIOT(diveceid, temperature, humidity, (int) (System.currentTimeMillis() / 1000));
//                System.out.println(nbiot.getDeviceID());
//                //模拟最新的一条数据
//                nbiotData.put(nbiot.getDeviceID(), nbiot);
//                System.out.println(nbiotData.size());
//                try {
//                    System.out.println(Thread.currentThread());
//                    Thread.sleep(5 * 1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        nbiotDataProducer.start();
    }

    @OnClose
    public void onClose(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "客户端[" + userNick + "]断开连接!";
        logger.info(message);
        //在连接关闭时停止向该客户端发送数据
        //否则在下次连接之后会出现直接开始发送实时数据的情况
        SESSION_SIGNAL.put(userNick, 0);
        WebSocketUtil.remoteSession(userNick);

    }

    @OnMessage
    public void OnMessage(@PathParam(value = "usernick") String userNick, String message) throws IOException, EncodeException {
        //
        String info = "客户端[" + userNick + "]发送消息：" + message;
        logger.info(info);
        //前端传回一个JSON对象，格式为{signal:0,sensorId:1,obsPropId:1}
        //接收到signal=0表示客户端暂停接收数据，signal=t表示前端希望每隔ts接收到服务端发送的数据
        JSONObject jsonObject = JSONObject.parseObject(message);
        int signal=(int)jsonObject.get("signal");
        String sensorId_obsPropId=jsonObject.getString("sensorId")+"_"+jsonObject.getString("obsPropId");
//        System.out.println(signal);
        if (signal<=0) {
            SESSION_SIGNAL.put(userNick, 0);
        }
        else {
            SESSION_SIGNAL.put(userNick, 1);
            Thread obsDataSender = new Thread(() -> {
                while (SESSION_SIGNAL.get(userNick)==1) {
                    Observation latestObs = RRALTIME_DATA.get(sensorId_obsPropId);
                    if (latestObs != null) {
                        logger.info("服务端发送实时数据: " + latestObs.toString());
                        try {
                            WebSocketUtil.sendMessage(WebSocketUtil.getSession(userNick), latestObs);
                        } catch (IOException | EncodeException e) {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        logger.info("没有可用的实时数据 传感器_观测能力ID:" + sensorId_obsPropId);
                        return;
                    }
                    try {
                        Thread.sleep(signal * 1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            obsDataSender.start();
        }

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("异常:" + throwable);
        try {
            session.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }

    public static void main(String[] args) {

    }
}
