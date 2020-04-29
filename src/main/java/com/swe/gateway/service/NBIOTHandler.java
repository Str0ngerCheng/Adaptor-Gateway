package com.swe.gateway.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.dao.NBIOTRepository;
import com.swe.gateway.model.NBIOT;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author lx
 */
@Component
public class NBIOTHandler {
    private final NBIOTRepository nbiotRepository;

    public NBIOTHandler() {
        nbiotRepository = new NBIOTRepository();
    }
    public Mono<String> insertNBIOTHandler(JSONObject jsonObject, String deviceID){
        return Mono.just(jsonObject).flatMap(monosink->{
            double temperature;
            double humidity;
            int timestamp=0;
            //最外一层
            //获取得是success,字符型;
            jsonObject.getString("msg");
            //获取得是0，字符型；
            jsonObject.getString("code");
            //第二层是数组
            JSONArray dataList = jsonObject.getJSONArray("data");
            double data[] = new double[100];
            for (int j = 0; j < dataList.size(); j++) {
                JSONObject list_obj =dataList.getJSONObject(j);
                if (list_obj != null) {
                    String item = list_obj.getString("item");
                    String dtime = list_obj.getString("dtime");
                    timestamp=Integer.parseInt(dtime);
                    data[j] = Double.parseDouble(list_obj.getString("data"));
                }

            }
            temperature = data[0];
            humidity = data[1];
            NBIOT nbiot=new NBIOT(deviceID,temperature,humidity,timestamp);
            nbiotRepository.insertNBIOT(nbiot);
            return Mono.just(nbiot.toString());
        });

    }
}
