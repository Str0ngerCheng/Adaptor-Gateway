package com.swe.gateway.controller;


import com.swe.gateway.model.City;
import com.swe.gateway.service.CityHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * create by jack 2018/4/21
 * webflux基于注解的控制器
 */
@RestController
@RequestMapping("/city")
public class CityWebFluxController {

    @Autowired
    private CityHandler cityHandler;

    @GetMapping(value = "/{id}")
    public Mono<City> findCityById(@PathVariable("id") Long id) {
        return cityHandler.findCityById(id);
    }

    @GetMapping()
    public Flux<City> findAllCity() {
        return cityHandler.findAllCity();
    }

    @PostMapping(value="/set", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Long> saveCity(ServerHttpRequest serverHttpRequest) {
       serverHttpRequest.getBody().subscribe(
               value -> System.out.println(value.read()),
               error -> error.printStackTrace());
        //获取request body
        return  null;
       // return cityHandler.save(city);
    }

    @PutMapping()
    public Mono<Long> modifyCity(@RequestBody City city) {
        System.out.println();

        return null;
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Long> deleteCity(@PathVariable("id") Long id) {
        return cityHandler.deleteCity(id);
    }

}
