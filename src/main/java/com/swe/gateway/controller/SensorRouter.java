package com.swe.gateway.controller;

import com.swe.gateway.service.SensorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class SensorRouter {
    @Bean
    public RouterFunction<ServerResponse> sensorRoute(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensor/{name}/{protocol}")
                .and(accept(MediaType.ALL)),handler::getSensorsByName);
    }
}

