package com.swe.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.dao.UavGpsMapper;
import com.swe.gateway.model.UavGps;
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
public class UavGpsHandler {
    @Autowired
    UavGpsMapper uavGpsMapper;

    public Mono<ServerResponse> getUavGpsByUavId(ServerRequest request) {
        String uavId =  request.queryParams().getFirst("uavId");
        List<UavGps> uavGps=uavGpsMapper.getUavGpsByUavId(uavId);

        Flux<UavGps> uavGpsFlux = Flux.fromIterable(uavGps);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(uavGpsFlux,UavGps.class);
    }
    
    public Mono<ServerResponse> getLatestUavGps(ServerRequest request){
        String uavId =  request.queryParams().getFirst("uavId");
        UavGps uavGps=uavGpsMapper.getLatestUavGps(uavId);

        Flux<UavGps> uavGpsFlux = Flux.just(uavGps);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(uavGpsFlux,UavGps.class);
    }
    
    public Mono<ServerResponse> add(ServerRequest request) {
        Mono<String> str = request.bodyToMono(String.class);
        return str.flatMap(s -> {
            JSONObject json = JSONObject.parseObject(s);
            UavGps uavGps=new UavGps();
            uavGps.setUavId(json.getJSONObject("params").getString("uavId"));
            uavGps.setLat(json.getJSONObject("params").getDouble("lat"));
            uavGps.setLon(json.getJSONObject("params").getDouble("lon"));
            uavGps.setAlt(json.getJSONObject("params").getDouble("alt"));
            uavGps.setReceivingTime(json.getJSONObject("params").getDate("receivingTime"));
            String type=json.getJSONObject("params").getString("type");

            UavGps test=uavGpsMapper.getUavGpsByUavIdAndDate(uavGps.getUavId(),uavGps.getReceivingTime());

            if(test==null) {
                uavGpsMapper.addUavGps(uavGps);
            }

            UavGps result=uavGpsMapper.getUavGpsByUavIdAndDate(uavGps.getUavId(),uavGps.getReceivingTime());
            int isDone=1;

            if(result==null) {
                uavGpsMapper.addUavGps(uavGps);
            }

            Flux<Integer> uavGpsFlux = Flux.just(isDone);

            return ServerResponse.ok().contentType(APPLICATION_JSON)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(uavGpsFlux, Integer.class);
        });
    }
}
