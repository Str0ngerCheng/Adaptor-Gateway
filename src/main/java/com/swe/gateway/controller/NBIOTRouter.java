package com.swe.gateway.controller;

import com.swe.gateway.model.NBIOT;
import com.swe.gateway.service.NBIOTHandler;
import com.swe.gateway.service.ObservationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class NBIOTRouter {
    @Bean
    public RouterFunction<ServerResponse> NBIOTRoute(NBIOTHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/nbiot/insert")
                .and(accept(MediaType.ALL)),handler::insertNBIOTHandler);
    }
}
