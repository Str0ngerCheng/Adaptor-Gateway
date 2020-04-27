package com.swe.gateway.controller;

import com.swe.gateway.service.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
@Configuration
public class LoginRouter {

    @Bean
    public RouterFunction<ServerResponse> loginRoute(LoginHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/Vue-Admin/login")
                .and(accept(MediaType.APPLICATION_JSON)),handler::login);
    }
}
