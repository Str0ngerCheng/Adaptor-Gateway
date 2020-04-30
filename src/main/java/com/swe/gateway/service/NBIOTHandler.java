package com.swe.gateway.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.dao.NBIOTRepository;
import com.swe.gateway.model.NBIOT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author lx
 */
@Component
public class NBIOTHandler {

    @Autowired
    NBIOTRepository nbiotRepository;

    public Mono<ServerResponse> insertNBIOTHandler(ServerRequest request) {
        String deviceID = request.pathVariable("deviceID");
        JSONObject jsonObject = JSONObject.parseObject(request.queryParams().toString());
        double temperature;
        double humidity;
        int timestamp = 0;
        //最外一层
        //获取得是success,字符型;
        jsonObject.getString("msg");
        //获取得是0，字符型；
        jsonObject.getString("code");
        //第二层是数组
        JSONArray dataList = jsonObject.getJSONArray("data");
        double data[] = new double[100];
        for (int j = 0; j < dataList.size(); j++) {
            JSONObject list_obj = dataList.getJSONObject(j);
            if (list_obj != null) {
                String item = list_obj.getString("item");
                String dtime = list_obj.getString("dtime");
                timestamp = Integer.parseInt(dtime);
                data[j] = Double.parseDouble(list_obj.getString("data"));
            }

        }
        temperature = data[0];
        humidity = data[1];
        NBIOT nbiot = new NBIOT(deviceID, temperature, humidity, timestamp);
        nbiotRepository.insertNBIOT(nbiot);
        return ServerResponse.ok().body(Mono.empty(), String.class);

    }
}
