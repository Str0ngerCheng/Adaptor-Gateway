
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
        return RouterFunctions.route(RequestPredicates.GET("/sensor/getSensorsByName")
                .and(accept(MediaType.ALL)),handler::getSensorsByName);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute1(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensor/getSensorsByType")
                .and(accept(MediaType.ALL)),handler::getSensorsByType);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute2(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensor/getHistoryData")
                .and(accept(MediaType.ALL)),handler::getHistoryData);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute3(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/sensor/add")
                .and(accept(MediaType.ALL)),handler::add);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute4(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensor/Delete")
                .and(accept(MediaType.ALL)),handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute5(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/sensor/update")
                .and(accept(MediaType.ALL)),handler::update);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute6(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensor/list")
                .and(accept(MediaType.ALL)),handler::list);
    }

    @Bean
    public RouterFunction<ServerResponse> sensorRoute7(SensorHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensor/allList")
                .and(accept(MediaType.ALL)),handler::allList);
    }


}


