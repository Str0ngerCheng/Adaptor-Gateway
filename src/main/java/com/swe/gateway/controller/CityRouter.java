package com.swe.gateway.controller;


import com.swe.gateway.service.CityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class CityRouter {
    @Bean
    public RouterFunction<ServerResponse> postroute(CityHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/test/listCity")
                .and(accept(MediaType.APPLICATION_JSON)),handler::listCity);
    }

    @Bean
    public RouterFunction<ServerResponse> postroute1(CityHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/test/1").and(accept(MediaType.APPLICATION_JSON)),handler::test)
                .and(RouterFunctions.route(RequestPredicates.GET("/test/2"),handler::testWebClient));
    }
}