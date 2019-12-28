package com.swe.gateway.controller;


import com.swe.gateway.model.City;
import com.swe.gateway.service.CityHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value="/set")
    public Mono<Long> saveCity() {
        City city=new City ();
        city.setId (1L);
        city.setProvinceId (2L);
        city.setCityName ("Wuhan");
        city.setDescription ("Different Every Day");
        return cityHandler.save(city);
    }

    @PutMapping()
    public Mono<Long> modifyCity(@RequestBody City city) {
        return cityHandler.modifyCity(city);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Long> deleteCity(@PathVariable("id") Long id) {
        return cityHandler.deleteCity(id);
    }

}
