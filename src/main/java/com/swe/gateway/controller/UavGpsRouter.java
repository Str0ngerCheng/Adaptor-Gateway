
package com.swe.gateway.controller;

import com.swe.gateway.service.UavGpsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class UavGpsRouter {
    @Bean
    public RouterFunction<ServerResponse> UavGpsRoute(UavGpsHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/uavGps/getUavGpsByUavId")
                .and(accept(MediaType.ALL)),handler::getUavGpsByUavId);
    }
    @Bean
    public RouterFunction<ServerResponse> UavGpsRoute1(UavGpsHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/uavGps/getLatestUavGps")
                .and(accept(MediaType.ALL)),handler::getLatestUavGps);
    }
    @Bean
    public RouterFunction<ServerResponse> UavGpsRoute2(UavGpsHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/uavGps/add")
                .and(accept(MediaType.ALL)),handler::add);
    }
}
