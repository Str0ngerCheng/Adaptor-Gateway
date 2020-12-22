package com.swe.gateway.config;

import com.swe.gateway.service.ZigbeeHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpServer;

/**
 * @author cbw
 * @date 2020/11/28 14:24
 */
@Configuration
public class TCPConfig {
    @Bean
    CommandLineRunner serverRunner(ZigbeeHandler zigbeeHandler) {
        return strings -> {
            createZigbeeServer (zigbeeHandler);
        };
    }

    /**
     * 创建TCP Server
     *
     * @param zigbeeHandler： 解析TCP Client上报数据的handler
     */
    private void createZigbeeServer(ZigbeeHandler zigbeeHandler) {
        TcpServer.create ( )
                .handle ((in, out) -> {
                    in.receive ( )
                            .asByteArray ( )
                            .subscribe ( );
                    return Flux.never ( );

                })
                .doOnConnection (conn -> conn
                        .addHandler ("encoder", zigbeeHandler.new EncoderHandler ( ))
                        .addHandler ("decoder", zigbeeHandler.new DecoderHandler ( ))
                        .addHandler ("handler", zigbeeHandler.new TcpHandler ( ))
                ) //实例只写了如何添加handler,可添加delimiter，tcp生命周期，decoder，encoder等handler
                .port (502)
                .bindNow ();
    }

}
