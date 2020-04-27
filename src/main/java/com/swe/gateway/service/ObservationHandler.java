package com.swe.gateway.service;

import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.model.Observation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Random;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ObservationHandler {
    @Autowired
    ObservationMapper observationMapper;
    public Mono<ServerResponse> saveObsBatch(ServerRequest request) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        DateTime dateTime=new DateTime();
        Random random = new Random();
        for(int i=1;i<=30;i++){
            Integer day=Integer.valueOf(df.format(dateTime.minusDays(i).toDate()));
            int[] hours={8,18};
            for(int j=0;j<hours.length;j++){
                Observation obs=new Observation();
                obs.setSensorId(3);
                obs.setObsPropId(1);
                obs.setDay(day);
                obs.setHour(hours[j]);
                obs.setTimestamp(dateTime.toDate());
                obs.setObsValue(String.valueOf(random.nextInt(40)));
                observationMapper.insert(obs);
            }
        }
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(Mono.just(""),String.class);
    }

}
