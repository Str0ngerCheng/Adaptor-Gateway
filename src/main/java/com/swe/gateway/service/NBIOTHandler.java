package com.swe.gateway.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.dao.*;
import com.swe.gateway.model.Observation;
import com.swe.gateway.model.ObservationProperty;
import com.swe.gateway.model.Sensor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author lx
 */
@Component
public class NBIOTHandler {

    private static final Logger logger = LogManager.getLogger(NBIOTHandler.class.getName());
    @Autowired
    NBIOTRepository nbiotRepository;
    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationMapper observationMapper;
    @Autowired
    SensorObsPropMapper sensorObsPropMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    public Mono<ServerResponse> insertNBIOTHandler(ServerRequest request) {
        Mono<String> str = request.bodyToMono(String.class);
        return str.flatMap(s -> {

            praseAndSaveNBIOTData(s);

            return ServerResponse.ok().contentType(APPLICATION_JSON)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(Mono.just("insert nbiot observation"), String.class);
        });

    }

    private void praseAndSaveNBIOTData(String s) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(s);
            String cpuId = jsonObject.getString("U");
            Integer signalQ = jsonObject.getInteger("Q");//信号强度
            Integer voltage = jsonObject.getInteger("V");//电压

            JSONArray hrArray = JSONArray.parseArray(jsonObject.getString("sHR"));
            JSONArray tmpArray = JSONArray.parseArray(jsonObject.getString("sTMP"));

            Double HR = (double) (hrArray.getInteger(0) / 100);
            Double TMP = (double) (tmpArray.getInteger(0) / 100);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            Integer day = Integer.valueOf(df.format(date));

            Sensor sensor = sensorMapper.getSensorByName("NBIOT-" + cpuId);

            Observation obs_TMP = new Observation();
            obs_TMP.setSensorId(sensor.getSensorId());
            obs_TMP.setObsPropId(1);//土壤温度
            obs_TMP.setDay(day);
            obs_TMP.setHour(date.getHours());
            obs_TMP.setTimestamp(date);
            obs_TMP.setObsValue(TMP.toString());

            Observation obs_HR = new Observation();
            obs_HR.setSensorId(sensor.getSensorId());
            obs_HR.setObsPropId(2);//土壤湿度
            obs_HR.setDay(day);
            obs_HR.setHour(date.getHours());
            obs_HR.setTimestamp(date);
            obs_HR.setObsValue(HR.toString());

            logger.info("insert nb_HR data cpuId :" + cpuId + ",row:" + observationMapper.insert(obs_HR));
            logger.info("insert nb_TMP data cpuId :" + cpuId + ",row:" + observationMapper.insert(obs_TMP));

            //ws 实时数据
            RealTimeHandler.REALTIME_DATA.put(sensor.getSensorName()+"_土壤温度",obs_TMP);
            RealTimeHandler.REALTIME_DATA.put(sensor.getSensorName()+"_土壤湿度",obs_HR);
        } catch (Exception e) {
            logger.error("praseAndSaveNBIOTData error: "+e);
        }
    }

}
