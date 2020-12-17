package com.swe.gateway.test;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


public class HttpConnection {

    public static String sendPost(String urlParam,JSONObject json){
        URLConnection con = null;

        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;
        PrintWriter out=null;

        try {
            URL url = new URL(urlParam);
            con = url.openConnection();

            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);

            out=new PrintWriter(con.getOutputStream());
            out.print(json);
            out.flush();
            //得到响应流
            InputStream inputStream = con.getInputStream();
            //将响应流转换成字符串
            resultBuffer = new StringBuffer();
            String line;
            buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = buffer.readLine()) != null) {
                resultBuffer.append(line);
            }
            return resultBuffer.toString();

        }catch(Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static String sendPost(String urlParam){
        URLConnection con = null;

        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;
        PrintWriter out=null;

        try {
            URL url = new URL(urlParam);
            con = url.openConnection();

            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);

            //得到响应流
            InputStream inputStream = con.getInputStream();
            //将响应流转换成字符串
            resultBuffer = new StringBuffer();
            String line;
            buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = buffer.readLine()) != null) {
                resultBuffer.append(line);
            }
            return resultBuffer.toString();

        }catch(Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
     /*   String name= URLEncoder.encode("土壤湿度","utf-8");
        String url ="http://127.0.0.1:8080/sensor/delete?sensorName=NBIOT-001&type="+name;*/
        String url ="http://127.0.0.1:8080/sensor/list";


      /* JSONObject json1=new JSONObject();
        json1.put("sensorName","ZigBee-008");
        json1.put("is_insitu","1");
        json1.put("location","ssss");
        json1.put("status","1");
        json1.put("protocol","ZigBee");
        json1.put("decription","HHHH");
        json1.put("oldtype","土壤湿度");
        json1.put("newtype","土壤温度");*/
        System.out.println(sendPost(url));
    }
}


