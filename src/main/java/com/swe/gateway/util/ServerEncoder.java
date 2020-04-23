package com.swe.gateway.util;


import com.swe.gateway.model.NBIOT;
import net.sf.json.JSONObject;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class ServerEncoder implements Encoder.Text<NBIOT>  {
    //代表websocket调用sendObject方法返回客户端的时候，必须返回的是DomainResponse对象
    @Override
    public String encode(NBIOT domainResponse) {
        //将java对象转换为json字符串
        return JSONObject.fromObject(domainResponse).toString();
    }
    @Override
    public void init(EndpointConfig endpointConfig) { }

    @Override
    public void destroy() { }
}