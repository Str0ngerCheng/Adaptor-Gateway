package com.swe.gateway.service;

import com.swe.gateway.dao.ObservationPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ObsPropHandler {
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;

    public Mono<ServerResponse> getAllObsPropNames(ServerRequest request) {

        Map<String,List<String>> map=new HashMap<>();
        List<String> obsPropNames=observationPropertyMapper.getAllObsPropNames();
        Collections.sort(obsPropNames);
        map.put("data",obsPropNames);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(Mono.just(map), Map.class);
    }
}
