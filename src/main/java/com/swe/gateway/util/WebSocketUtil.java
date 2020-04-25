package com.swe.gateway.util;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketUtil {

    /**
     * 简单使用map进行存储在线的session
     */
    private static final ConcurrentHashMap<String, Session> ONLINE_SESSION = new ConcurrentHashMap<>();


    public static void addSession(String userNick, Session session) {
        //putIfAbsent 添加键—值对的时候，先判断该键值对是否已经存在
        //不存在：新增，并返回null
        //存在：不覆盖，直接返回已存在的值
//        ONLINE_SESSION.putIfAbsent(userNick, session);
        //简单示例 不考虑复杂情况。。怎么简单怎么来了
        ONLINE_SESSION.put(userNick, session);
    }

    public static Session getSession(String uerNick) {
        return ONLINE_SESSION.get(uerNick);
    }

    public static void remoteSession(String userNick) {
        ONLINE_SESSION.remove(userNick);
    }

    /**
     * 向某个用户发送消息
     *
     * @param session 某一用户的session对象
     * @param message
     */
    public static void sendMessage(Session session, Object message) throws IOException, EncodeException {
        if (session == null) {
            return;
        }
        // getAsyncRemote()和getBasicRemote()异步与同步
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        //发送消息
        basic.sendObject(message);
    }

    /*
     *
     * Interval：秒，发送间隔
     * */
    public static void sendMessageContinue(String sessionName, Object message, int interval) throws IOException, EncodeException {
        Session session = WebSocketUtil.getSession(sessionName);
        if (session == null) {
            return;
        }
        while (true) {
            RemoteEndpoint.Basic basic = session.getBasicRemote();
            //发送消息
            basic.sendObject(message);
            try {
                Thread.sleep(interval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向所有在线人发送消息
     *
     * @param message
     */
    public static void sendMessageForAll(Object message) {
        //jdk8 新方法
        ONLINE_SESSION.forEach((sessionId, session) -> {
            try {
                sendMessage(session, message);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        });
    }
}
