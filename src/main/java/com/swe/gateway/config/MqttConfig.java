package com.swe.gateway.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author cbw
 */
public class MqttConfig {
    private static String host = "tcp://127.0.0.1:61613";
    private static String userName = "admin";
    private static String passWord = "password";
    private static String locationTopic = "/localization/pose";
    private static String pictureTopic = "/pic";
    private static MqttClient client = null;

    public static MqttClient getMqttClient(){
        if(client==null){
            synchronized (host){
                if(client==null){
                    try {
                        client = new MqttClient (host, "CallbackClient", new MemoryPersistence ( ));
                        MqttConnectOptions options = new MqttConnectOptions ( );
                        //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
                        //这里设置为true表示每次连接到服务器都以新的身份连接
                        options.setCleanSession (true);
                        //设置连接的用户名
                        options.setUserName (userName);
                        //设置连接的密码
                        options.setPassword (passWord.toCharArray ( ));
                        // 设置超时时间 单位为秒
                        options.setConnectionTimeout (10);
                        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
                        options.setKeepAliveInterval (20);
                        //链接
                        client.connect (options);
                        //订阅
                        client.subscribe (locationTopic, 0);
                        client.subscribe (pictureTopic, 0);
                    } catch (MqttException e) {
                        e.printStackTrace ( );
                    }

                }
            }
        }
        return client;
    }
}
