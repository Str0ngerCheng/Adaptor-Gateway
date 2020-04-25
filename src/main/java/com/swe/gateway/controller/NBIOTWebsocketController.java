package com.swe.gateway.controller;

import com.swe.gateway.model.NBIOT;
import com.swe.gateway.util.ServerEncoder;
import com.swe.gateway.util.WebSocketUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/nbiot/{usernick}", encoders = {ServerEncoder.class})
public class NBIOTWebsocketController {
    //用了一个Map存储传感器的实时数据，只需要存每个设备的最新一条数据就可以了
    //前端点击发送一个传感器的设备id，然后去获取传感器的最新一条数据
    //可以用http://coolaf.com/tool/chattest测试ws的接口
    private static final ConcurrentHashMap<String, NBIOT> NBIOT_DATA = new ConcurrentHashMap<>();
    //这个变量去判断是否继续向当前session持续发送消息
    //不用interrupt()是因为在调用isinterrupted()判断之后标志位又会被置为true，另外这样可以控制向多个客户端的发送情况
    private static final ConcurrentHashMap<String, Integer> SESSION_SINGLAL = new ConcurrentHashMap<>();
    private static final  Logger logger = LogManager.getLogger(NBIOTWebsocketController.class.getName());
    @OnOpen
    public void onOpen(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "客户端[" + userNick + "]建立连接!";
        logger.info(message);
        WebSocketUtil.addSession(userNick, session);
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
        SESSION_SINGLAL.put(userNick, 0);
        WebSocketUtil.remoteSession(userNick);

    }

    @OnMessage
    public void OnMessage(@PathParam(value = "usernick") String userNick, String message) throws IOException, EncodeException {
        //
        String info = "客户端[" + userNick + "]发送消息：" + message;
        logger.info(info);
        //接收到0表示客户端暂停接收数据
        //否则服务端向名为uerNick的客户端发送id为message的传感器实时数据
        if (message.equals("0")) {
            SESSION_SINGLAL.put(userNick, 0);
        }
        else {
            SESSION_SINGLAL.put(userNick, 1);
            Thread nbiotDataSender = new Thread(() -> {
                while (SESSION_SINGLAL.get(userNick)==1) {
                    NBIOT nbiot = NBIOT_DATA.get(message);
                    if (nbiot != null) {
                        logger.info("服务端发送实时数据: " + nbiot.toString());
                        try {
                            WebSocketUtil.sendMessage(WebSocketUtil.getSession(userNick), NBIOT_DATA.get(message));
                        } catch (IOException | EncodeException e) {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        logger.info("没有可用的实时数据 传感器ID:" + message);
                        return;
                    }
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            nbiotDataSender.start();
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
