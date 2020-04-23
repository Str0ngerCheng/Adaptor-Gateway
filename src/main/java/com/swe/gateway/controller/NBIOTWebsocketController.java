package com.swe.gateway.controller;

import com.swe.gateway.model.NBIOT;
import com.swe.gateway.util.ServerEncoder;
import com.swe.gateway.util.WebSocketUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/nbiot/{usernick}", encoders = {ServerEncoder.class})
public class NBIOTWebsocketController {
    //用了一个Map存储传感器的
    //前端点击发送一个传感器的name，然后去获取传感器的最新一条数据并放入Map中
    private static final ConcurrentHashMap<String, NBIOT> nbiotData = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "客户端[" + userNick + "]建立连接!";
        System.out.println(message);
        WebSocketUtil.addSession(userNick, session);
        while (true) {
            double temperature = (Math.random() * 1000) % 30;
            double humidity = (Math.random() * 1000) % 60;
            NBIOT nbiot = new NBIOT("NBIOT-NODE-1", temperature, humidity, (int)(System.currentTimeMillis() / 1000));
            //模拟最新的一条数据
            nbiotData.put(nbiot.getDeviceID(), nbiot);

            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    @OnClose
    public void onClose(@PathParam(value = "usernick") String userNick, Session session) {
        String message = "客户端[" + userNick + "]断开连接!";
        System.out.println(message);
        WebSocketUtil.remoteSession(userNick);

    }

    @OnMessage
    public void OnMessage(@PathParam(value = "usernick") String userNick, String message) throws IOException, EncodeException {
        //类似群发
        String info = "客户端[" + userNick + "]：" + message;
        System.out.println(message);
        while (true) {
            nbiotData.get(message);
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("异常:" + throwable);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }

}
