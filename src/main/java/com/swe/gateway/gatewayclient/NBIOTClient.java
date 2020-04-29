package com.swe.gateway.gatewayclient;

import com.swe.gateway.config.NBIOTConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

/**
 * @author lx
 */
public class NBIOTClient {
    public static final Logger logger = LogManager.getLogger(NBIOTClient.class);
    final private WebClient webClient = WebClient.create();

    public void transmit(String deviceID) {
        String getURL = "http://jingkongyun.com/monitorcloud/platform/gemho/apiDev/getDataByDevice?" + "deviceId=" + deviceID + "&start=" + (System.currentTimeMillis() / 1000 - 3800) + "&end=" + System.currentTimeMillis() / 1000;
        String postURL = "localhost:8081/nbiot/" + deviceID;
        Mono<String> resp = webClient.get()
                .uri(getURL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class);
        resp.subscribe(s -> logger.info("deviceID=" + deviceID + " msg=" + s.split("\"")[3]));
        resp.subscribe(s -> {
            Mono<String> postresp = webClient.post()
                    .uri(postURL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(s), String.class)
                    .retrieve().bodyToMono(String.class);
            postresp.subscribe(ps -> logger.info(ps));
        });
    }

    public static void main(String[] args) {
        NBIOTClient nbiotClient = new NBIOTClient();
        while (true) {
            Iterator iterator = NBIOTConfig.deviceID_SOSID.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                nbiotClient.transmit((String) entry.getKey());
            }
            try {
                Thread.sleep(1000 *3600);
                //Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
