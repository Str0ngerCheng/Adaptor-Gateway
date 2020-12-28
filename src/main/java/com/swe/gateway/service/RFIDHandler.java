package com.swe.gateway.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.swe.gateway.config.MqttConfig;
import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.dao.SensorObsPropMapper;
import com.swe.gateway.model.Observation;
import com.swe.gateway.model.ObservationProperty;
import com.swe.gateway.model.Sensor;
import com.swe.gateway.model.SensorObsProp;
import com.swe.gateway.util.WebSocketSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cbw
 * @date 2020/12/11 18:40
 */
@Component
public class RFIDHandler{
    private static final Logger logger = LogManager.getLogger (RFIDHandler.class.getName ( ));
    private Map<String, Observation> rfidDataMap = RealTimeHandler.REALTIME_DATA;

    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationMapper observationMapper;
    @Autowired
    SensorObsPropMapper sensorObsPropMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    public RFIDHandler() {
        MqttClient client = MqttConfig.getRFIDClient ( );
        client.setCallback (new MqttCallback ( ) {
            @Override
            public void messageArrived(String topicName, MqttMessage mqttMessage) throws Exception {
                //subscribe后得到的消息会执行到这里面
               /* System.out.print ("messageArrived: ");
                System.out.println (topicName + "---" + mqttMessage.toString ( ));*/
                String msg = mqttMessage.toString ( );
                if ("rptall".equals (topicName) && !"close".equals (msg)) {
                    logger.info(mqttMessage.toString());
                    JSONArray sensors = JSONObject.parseObject (mqttMessage.toString ( )).getJSONArray ("list");
                    SimpleDateFormat df = new SimpleDateFormat ("yyyyMMdd");
                    Date date = new Date ( );
                    Integer day = Integer.valueOf (df.format (date));
                    for (int i = 0; i < sensors.size ( ); i++) {
                        JSONObject tag = sensors.getJSONObject (i);
                        String tagTyp = tag.getString ("tagTyp");
                        Sensor sensor = sensorMapper.getSensorByName ("RFID-" + tag.getString ("tagID"));
                        if ("1".equals (tagTyp)) {
                            String[] values = tag.getString ("tagDat").split (";");
                            for (int j = 0; j < values.length; j++) {
                                Observation obs = new Observation ( );
                                obs.setSensorId (sensor.getSensorId ( ));
                                obs.setObsPropId (6 + j);
                                obs.setDay (day);
                                obs.setHour (date.getHours ( ));
                                obs.setTimestamp (date);
                                obs.setObsValue (values[j]);
                                //ws传输
                                if (j == 0) {
                                    rfidDataMap.put (sensor.getSensorName ( ) + "_环境温度", obs);
                                } else rfidDataMap.put (sensor.getSensorName ( ) + "_环境湿度", obs);
                                //入库
                                observationMapper.insert (obs);
                            }
                        } else {
                            String value = tag.getString ("tagDat");
                            List<SensorObsProp> sensorObsProps = sensorObsPropMapper.getBySensorId (sensor.getSensorId ( ));
                            for (SensorObsProp sensorObsProp : sensorObsProps) {
                                Observation obs = new Observation ( );
                                obs.setSensorId (sensor.getSensorId ( ));
                                obs.setObsPropId (sensorObsProp.getObsPropId ( ));
                                obs.setDay (day);
                                obs.setHour (date.getHours ( ));
                                obs.setTimestamp (date);
                                obs.setObsValue (value);

                                //ws传输
                                ObservationProperty obsProp = observationPropertyMapper.getObsPropById (sensorObsProp.getObsPropId ( ));
                                rfidDataMap.put (sensor.getSensorName ( ) + "_" + obsProp.getObsPropName ( ), obs);
                                //入库
                                observationMapper.insert (obs);

                            }
                        }


                    }
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
                System.out.println("RFID connectionLost----------");
                while (true) {
                    if (!client.isConnected()) {
                        synchronized (client) {
                            if (!client.isConnected()) {
                                try {
                                    client.reconnect();
                                    Thread.sleep(1000);
                                    System.out.println("RFID MQTT try to reconnect----------" + client.isConnected());
                                } catch (MqttException e) {
                                   /* e.printStackTrace();*/
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else {
                        System.out.println("RFID MQTT reconnect success----------");
                        break;
                    }

                }
            }
        });
    }

}

