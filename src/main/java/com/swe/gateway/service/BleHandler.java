package com.swe.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.model.BleData;
import com.swe.gateway.model.Observation;
import com.swe.gateway.util.WebSocketSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BleHandler implements CommandLineRunner, WebSocketHandler {

    private static final Logger logger = LogManager.getLogger (BleHandler.class.getName ( ));

    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationMapper observationMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    private Map<String, Observation> bleDataMap = RealTimeHandler.REALTIME_DATA;
    private ConcurrentHashMap<String, WebSocketSender> senderMap = new ConcurrentHashMap<> ( );
    private Boolean isSocketOn = false;
    private String[] A = {"A1", "A2", "A3", "A4"};
    private String[] B = {"B1", "B2", "B3", "B4"};
    private Map<String, Integer> sensorIds = new HashMap<>();
    private Map<String, BleData> realTimeDataMap = new ConcurrentHashMap();

    public BleHandler() {
        sensorIds.put("A", 4);
        sensorIds.put("B", 44);
    }

    private void getBleData(String[] str) {
        //文件io流
        File file = null;
        // 初始化字符输入流
        Reader fileReader = null;
        while (true) {
            //观测时间
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Integer day = Integer.valueOf(df.format(date));
            //实时数据
            BleData bleData =new BleData();
            for (int i = 0; i < str.length; i++) {
                try {
                    file = new File("C:\\Users\\SWE\\Desktop\\Bluetooth demo\\" + str[i] + ".txt");
                    fileReader = new FileReader(file);
                    char[] charArray = new char[50];
                    // 一次读取一个数组长度的字符串
                    fileReader.read(charArray);
                    String value = "";
                    for (char cha : charArray) {
                        value += cha;
                    }
                    value = value.replace("\r\n","").replace("\u0000","");
                    if(str[i].contains("1")) {
                        bleData.setAmp(value);
                    }else if(str[i].contains("2")){
                        bleData.setPow(value);
                    }else if(str[i].contains("3")){
                        bleData.setAvg(value);
                    }else{
                        bleData.setMas(value);
                        if(str[i].contains("A")){
                            realTimeDataMap.put("A",bleData);
                        }else realTimeDataMap.put("B",bleData);

                    }
                    Integer sensorId;
                    if (str[i].contains("A")) {
                        sensorId = 4;
                    } else {
                        sensorId = 44;
                    }
                    Observation obs = new Observation();
                    obs.setSensorId(sensorId);
                    obs.setObsPropId(i + 14);
                    obs.setDay(day);
                    obs.setHour(date.getHours());
                    obs.setTimestamp(date);
                    obs.setObsValue(value);
                    observationMapper.insert(obs);
                    String sensorName = sensorMapper.getSensorById(sensorId).getSensorName();
                    String obsPropName = observationPropertyMapper.getObsPropById(i + 14).getObsPropName();
                    bleDataMap.put(sensorName + "_" + obsPropName, obs);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } finally {
                    if (fileReader != null) {
                        try {
                            // 关闭流过程，也有可能出现异常
                            fileReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getBleData(A);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getBleData(B);
            }
        }).start();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionid = session.getId();
        Mono<Void> output = session.
                send(Flux.create(sink ->
                        senderMap.put(sessionid, new WebSocketSender(session, sink))));
        return output.doOnSubscribe(s ->
                {
                    logger.info("客户端[" + sessionid + "]建立连接");
                    isSocketOn = true;
                    new Thread(() -> {
                        while (isSocketOn) {
                            try {
                                WebSocketSender socketSender = senderMap.get(sessionid);
                                if (socketSender != null) {
                                    socketSender.sendData(JSONObject.toJSONString(realTimeDataMap, SerializerFeature.WriteMapNullValue));
                                }
                                Thread.sleep(5000);//五秒传输一次
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                })
                .doOnError(s -> {
                    logger.info("客户端[" + sessionid + "]发生错误" + s.getLocalizedMessage());
                    isSocketOn = false;
                    senderMap.remove(sessionid);
                    session.close();
                })
                .doOnSuccess(s -> {
                    isSocketOn = false;
                    senderMap.remove(sessionid);
                    session.close();
                    logger.info("客户端[" + sessionid + "]关闭连接");
                }).then();
    }
}
