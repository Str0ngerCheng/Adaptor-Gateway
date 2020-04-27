package com.swe.gateway.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
@Component
public class LoginHandler {
    public Mono<ServerResponse> login(ServerRequest request) {
        Mono <String> str = request.bodyToMono(String.class);
        return str.flatMap(s-> {
            System.out.println(s);
            JSONObject json = JSONObject.parseObject(s);//能够解析整个json串
            System.out.println(json.getJSONObject("params").getString("username"));
            return ServerResponse.notFound().build();
        });
    }
}
