package com.swe.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.swe.gateway.config.MqttConfig;
import com.swe.gateway.util.WebSocketSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cbw
 */
@Component
public class RobotHandler implements WebSocketHandler {
    private static final Logger logger = LogManager.getLogger (RobotHandler.class.getName ( ));
    private static final BlockingQueue<String> locationQueue = new ArrayBlockingQueue<> (1024); //缓冲区允许放1024个数据
    private static final BlockingQueue<String> pictureQueue = new ArrayBlockingQueue<> (1024); //缓冲区允许放1024个数据
    private ConcurrentHashMap<String, WebSocketSender> senderMap = new ConcurrentHashMap<> ( );
    private Boolean isSocketOn = false;

    public RobotHandler() {
        MqttClient client = MqttConfig.getMqttClient ( );
        client.setCallback (new MqttCallback( ){
            @Override
            public void messageArrived(String topicName, MqttMessage mqttMessage) throws Exception {
                //subscribe后得到的消息会执行到这里面
                System.out.print ("messageArrived: ");
                System.out.println (topicName + "---" + mqttMessage.toString ( ));
                if ("localization/pose".equals (topicName)) {
                    locationQueue.put (mqttMessage.toString ( ));
                } else if ("pic".equals (topicName)) {
                    pictureQueue.put(mqttMessage.toString ( ));
                    /*FileOutputStream fos = new FileOutputStream ("D:\\test1.jpg");
                    fos.write (mqttMessage.getPayload ( ));
                    fos.close ( );*/
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                //publish后会执行到这里
                System.out.println ("deliveryComplete---------"
                        + iMqttDeliveryToken.isComplete ( ));
            }

            public void connectionLost(Throwable cause) {
                // //连接丢失后，一般在这里面进行重连
                System.out.println ("connectionLost----------");
            }
        });
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionid = session.getId ( );
        Mono<Void> output = session.
                send (Flux.create (sink ->
                        senderMap.put (sessionid, new WebSocketSender (session, sink))));
        Mono<Void> input = session.receive ( )
                .map (WebSocketMessage::getPayloadAsText)
                .map (message -> {
                    isSocketOn = true;
                    String info = "接收到客户端[" + sessionid + "]发送的数据：" + message;
                    logger.info (info);
                    new Thread (() -> {
                        while (isSocketOn) {
                            try {
                                Thread.sleep (1000);//一秒传输一次
                                String location = locationQueue.poll ( );
                                String pic = pictureQueue.poll ( );
                                if (location != null || pic != null) {
                                    WebSocketSender socketSender = senderMap.get (sessionid);
                                    if (socketSender != null) {
                                        Map<String, Object> jsonMap = new HashMap ( );
                                        jsonMap.put ("location", location);
                                        jsonMap.put ("pic", pic);
                                        socketSender.sendData (JSONObject.toJSONString (jsonMap, SerializerFeature.WriteMapNullValue));
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace ( );
                            }
                        }
                    }).start ( );
                    return message;
                }).then ( );
        return Mono.zip (output, input)
                .doOnSubscribe (s -> logger.info ("客户端[" + sessionid + "]建立连接"))
                .doOnError (s -> {
                    logger.info ("客户端[" + sessionid + "]发生错误" + s.getLocalizedMessage ( ));
                    isSocketOn = false;
                    senderMap.remove (sessionid);
                    session.close ( );
                })
                .doOnSuccess (s -> {
                    isSocketOn = false;
                    senderMap.remove (sessionid);
                    session.close ( );
                    logger.info ("客户端[" + sessionid + "]关闭连接");
                }).then ( );
    }
}
