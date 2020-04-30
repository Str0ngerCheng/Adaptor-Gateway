package com.swe.gateway;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author cbw
 */
@SpringBootApplication
@MapperScan("com.swe.gateway.dao")

public class GateWayApplication {
    public static void main(String[] args) {

        SpringApplication.run(GateWayApplication.class, args);
    }
}
