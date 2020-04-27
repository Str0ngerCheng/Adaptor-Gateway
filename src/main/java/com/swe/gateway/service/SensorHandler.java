package com.swe.gateway.service;

import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.dto.ObservationDTO;
import com.swe.gateway.dto.SensorDTO;
import com.swe.gateway.model.Observation;
import com.swe.gateway.model.ObservationProperty;
import com.swe.gateway.model.Sensor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
@Component
public class SensorHandler {
    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;
    @Autowired
    ObservationMapper observationMapper;

    public Mono<ServerResponse> getSensorsByName(ServerRequest request) {
        String name =  request.queryParams().getFirst("sensorName");
        Sensor sensor=sensorMapper.getSensorByName(name);
        List<String> types=new ArrayList<>();
        if(sensor!=null)
            types =observationPropertyMapper.getTypesBySensorId(sensor.getSensorId());
        List<SensorDTO> sensorDTOS=new ArrayList<>();
        for(int i=0;i<types.size();i++){
            SensorDTO sensorDTO=new SensorDTO(sensor,types.get(i));
            sensorDTOS.add(sensorDTO);
        }
        Collections.sort(sensorDTOS);
        Flux<SensorDTO> sensorDTOFlux = Flux.fromIterable(sensorDTOS);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(sensorDTOFlux,SensorDTO.class);
    }

    public Mono<ServerResponse> getSensorsByType(ServerRequest request) {
        List<String> types= request.queryParams().get("type[]");
        String protocol= request.queryParams().getFirst("protocol");
        List<String>typeIds=observationPropertyMapper.getTypeIdsByTypes(types);
        List<SensorDTO> sensorDTOS=new ArrayList<>();
        for(int i=0;i<typeIds.size();i++){
            List<Sensor> sensors=sensorMapper.getSensorsByType(typeIds.get(i),protocol);
            for(int j=0;j<sensors.size();j++) {
                SensorDTO sensorDTO = new SensorDTO(sensors.get(j), types.get(i));
                sensorDTOS.add(sensorDTO);
            }
        }
        Collections.sort(sensorDTOS);
        Flux<SensorDTO> sensorDTOFlux = Flux.fromIterable(sensorDTOS);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(sensorDTOFlux,SensorDTO.class);
    }

    public Mono<ServerResponse> getHistoryData(ServerRequest request) {
        String name =  request.queryParams().getFirst("sensorName");
        String type= request.queryParams().getFirst("type");
        Integer sensorId=sensorMapper.getSensorByName(name).getSensorId();

        ObservationProperty obsProperty=observationPropertyMapper.getObsPropByType(type);
        Integer typeId=obsProperty.getObsPropId();
        String uom=obsProperty.getUom();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        DateTime dateTime=new DateTime();
        int start=Integer.valueOf(df.format(dateTime.minusDays(30).toDate()));
        int end=Integer.valueOf(df.format(dateTime.minusDays(1).toDate()));

        List<Observation> observations=observationMapper.getHistoryData(sensorId,typeId,start,end);
        ObservationDTO obsDTO=new ObservationDTO(name,type,uom,observations);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(Mono.just(obsDTO),ObservationDTO.class);
    }
}
