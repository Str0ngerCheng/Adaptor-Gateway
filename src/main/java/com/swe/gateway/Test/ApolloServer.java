package com.swe.gateway.Test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author cbw
 */
public class ApolloServer {
    private static String host = "tcp://127.0.0.1:61613";
    private static String userName = "admin";
    private static String passWord = "password";
    private static String[] topics = {"/localization/pose","/pic"};
    public static void  mqttPublisher() throws MqttException, IOException {
        // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        MqttClient client = new MqttClient (host, "CallbackServer", new MemoryPersistence ( ));
        MqttMessage message = new MqttMessage ( );
        MqttConnectOptions options = new MqttConnectOptions ( );
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession (true);
        // 设置连接的用户名
        options.setUserName (userName);
        // 设置连接的密码
        options.setPassword (passWord.toCharArray ( ));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout (10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval (20);
        client.setCallback (new MqttCallback ( ) {
            public void connectionLost(Throwable cause) {
                // //连接丢失后，一般在这里面进行重连
                System.out.println ("connectionLost----------");
            }
            @Override
            public void messageArrived(String topicName, MqttMessage mqttMessage) throws Exception {
                //subscribe后得到的消息会执行到这里面
                System.out.println ("messageArrived----------");
                System.out.println (topicName + "---" + message.toString ( ));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                //publish后会执行到这里
              /*  System.out.println ("deliveryComplete: "
                        + iMqttDeliveryToken.isComplete ( ));*/
            }

        });
        message.setQos (0);
        message.setRetained (true);
        System.out.println (message.isRetained ( ) + "------ratained状态");
        client.connect (options);
        int count = 1;
        while (true) {
            MqttTopic topic = null;
            MqttDeliveryToken token = null;
            if(count==9) count=1;
            if(count%4==0) {
                topic = client.getTopic (topics[1]);
                //获取图片字节流
                FileInputStream fis = new FileInputStream("D:\\test.jpg");
                byte[] buf = new byte[1024*1024];
                int len = 0;
                //往输出流里面投放数据
                while ((len = fis.read(buf)) != -1)
                {
                    byte[] tmp =new byte[len];
                    for(int i=0;i<len;i++){
                        tmp[i]=buf[i];
                    }
                    message.setPayload (tmp);
                    token = topic.publish (message);
                    token.waitForCompletion ( );
                }
            }else{
                topic = client.getTopic (topics[0]);
                SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                message.setPayload (("mqtt test " + df.format (new Date ( ))).getBytes ( ));
                token = topic.publish (message);
                token.waitForCompletion ( );
            }
            count++;
            System.out.println ("deliveryComplete: "+topic+" "
                    + token.isComplete ( ));
            try {
                Thread.sleep (10000);
            } catch (InterruptedException e) {
                e.printStackTrace ( );
            }
        }
    }
    public static void main(String[] args) throws MqttException, IOException {
        mqttPublisher ();

    }
}
