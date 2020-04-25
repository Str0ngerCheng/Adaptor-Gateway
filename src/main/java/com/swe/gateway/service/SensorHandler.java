package com.swe.gateway.service;

import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.dto.SensorDTO;
import com.swe.gateway.model.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
@Component
public class SensorHandler {
    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    public Mono<ServerResponse> getSensorsByName(ServerRequest request) {
        String name = request.pathVariable("name");
        String protocol=request.pathVariable("protocol");
        Sensor sensor=sensorMapper.getSensorsByName(name,protocol);
        List<String> types=observationPropertyMapper.getTypesBySensorId(sensor.getSensorId());
        List<SensorDTO> sensorDTOS=new ArrayList<>();
        for(int i=0;i<types.size();i++){
            SensorDTO sensorDTO=new SensorDTO(sensor,types.get(i));
            sensorDTOS.add(sensorDTO);
        }
        Flux<SensorDTO> sensorDTOFlux = Flux.fromIterable(sensorDTOS);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(sensorDTOFlux,SensorDTO.class);
    }
}
