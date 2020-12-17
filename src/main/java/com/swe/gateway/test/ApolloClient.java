package com.swe.gateway.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;


/**
 * @author cbw
 */
public class ApolloClient {
    private static String host = "tcp://218.6.70.26:1883";
    private static String rfidTopic = "rptall";

    public static void  mqttSubscriber() throws MqttException, IOException {
        // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        MqttClient client = new MqttClient (host, "CallbackServer", new MemoryPersistence ( ));
        MqttConnectOptions options = new MqttConnectOptions ( );
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession (true);
        // 设置超时时间 单位为秒
        options.setConnectionTimeout (10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval (20);
        client.connect (options);
        //订阅
        client.subscribe (rfidTopic, 2);

        client.setCallback (new MqttCallback ( ) {
            public void connectionLost(Throwable cause) {
                System.out.println ("connectionLost----------");
            }
            @Override
            public void messageArrived(String topicName, MqttMessage mqttMessage) throws Exception {
                //subscribe后得到的消息会执行到这里面
                System.out.println ("messageArrived----------");
                System.out.println (topicName + "---" + mqttMessage.toString ( ));
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }

        });
    }
    public static void main(String[] args) throws MqttException, IOException {
        mqttSubscriber ();

    }
}
