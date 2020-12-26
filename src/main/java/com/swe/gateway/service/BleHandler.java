package com.swe.gateway.service;

import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class BleHandler  implements CommandLineRunner {

    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationMapper observationMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    private Map<String, Observation> bleDataMap = RealTimeHandler.REALTIME_DATA;

    private String[] A = {"A1", "A2", "A3", "A4"};
    private String[] B = {"B1", "B2", "B3", "B4"};
    private Map<String, Integer> sensorIds = new HashMap<>();

    public BleHandler() {
        sensorIds.put("A", 4);
        sensorIds.put("B", 44);
    }

    private void getBleData(String[] str) {
        while (true) {
            File file = null;
            // 初始化字符输入流
            Reader fileReader = null;
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Integer day = Integer.valueOf(df.format(date));
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

                    String sensorName=sensorMapper.getSensorById(sensorId).getSensorName();
                    String obsPropName= observationPropertyMapper.getObsPropById(i+14).getObsPropName();
                    bleDataMap.put (sensorName + "_" + obsPropName, obs);
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
}
