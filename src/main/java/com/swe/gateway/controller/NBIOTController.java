package com.swe.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.service.NBIOTHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author lx
 */
@RestController
@RequestMapping(value = "/nbiot")
public class NBIOTController {
    @Autowired
    private NBIOTHandler nbiotHandler;


    @PostMapping(value = "/{deviceID}")
    public Mono<String> saveCity(@RequestBody JSONObject jsonObject, @PathVariable("deviceID")String deviceID) {
        return nbiotHandler.insertNBIOTHandler(jsonObject,deviceID);
    }

}
