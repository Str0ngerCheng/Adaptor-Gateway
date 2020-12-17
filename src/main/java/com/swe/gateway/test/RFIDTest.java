package com.swe.gateway.test;

import com.alibaba.fastjson.JSONObject;

/**
 * @author cbw
 * @date 2020/12/11 18:59
 */
public class RFIDTest {

    public static void main(String[] args) {
        String str ="{\"cmd\":\"1\",\"basID\":\"2020102701\",\"basVS\":\"2020-11-05 16:03 V1.00\",\"basPow\":\"4.97\",\"basBat\":\"0.03\",\"basTmp\":\"35.95\",list:[{\"tagID\":\"6000000147\",\"tagTyp\":\"2\",\"tagDat\":\"5\",\"tagAlm\":\"0\",\"tagBat\":\"2.7\",\"tagTim\":\"2020-12-11 18:54:55\",\"tagRSSI\":\"81\"},{\"tagID\":\"0300000001\",\"tagTyp\":\"3\",\"tagDat\":\"91\",\"tagAlm\":\"0\",\"tagBat\":\"3.4\",\"tagTim\":\"2020-12-11 18:54:55\",\"tagRSSI\":\"35\"},{\"tagID\":\"0100000001\",\"tagTyp\":\"1\",\"tagDat\":\"12;94\",\"tagAlm\":\"1\",\"tagBat\":\"2.8\",\"tagTim\":\"2020-12-11 18:54:57\",\"tagRSSI\":\"35\"}]}";

        String str1 = JSONObject.parseObject (str).getJSONArray ("list").getJSONObject (0).getString ("tagTyp");
        System.out.println (str1 );
    }
}
