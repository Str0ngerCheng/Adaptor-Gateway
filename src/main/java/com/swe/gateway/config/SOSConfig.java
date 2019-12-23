package com.swe.gateway.config;

/**
 * @author cbw
 */
public class SOSConfig {
    public static final int Socket_TimeOut = 200000;
    public static final String SOS_Url = "http://gsw.whu.edu.cn:8080/MongoSOS/sos";
    //     温度属性
    public static final String Temperature_ObsProperty="AirTemperature";
    public static final String Temperature_ObsResultName="空气温度";
    public static final String Temperature_ObsResultUom="Cel";

//     湿度属性

    public static final String Humidity_ObsProperty="AirHumidity";
    public static final String  Humidity_ObsResultName="空气湿度";
    public static final String  Humidity_ObsResultUom="RH";

    //   pm2.5
    public static final String PM_ObsProperty="PM2.5";
    public static final String PM_ObsResultName="PM2.5";
    public static final String  PM_ObsResultUom="mg/m3";


//     雨量属性

    public static final String RainFall_ObsProperty="Rainfall";
    public static final String RainFall_ObsResultName="雨量";
    public static final String  RainFall_ObsResultUom="mm/24h";

//     风速属性

    public static final String WindSpeed_ObsProperty="WindSpeed";
    public static final String WindSpeed_ObsResultName="风速";
    public static final String  WindSpeed_ObsResultUom="m/s";

    //     风向属性
    public static final String WindDirection_ObsProperty="WindDirection";
    public static final String WindDirection_ObsResultName="风向";
    public static final String  WindDirection_ObsResultUom="°";

    //     备用属性

    //     TVOC属性
    public static final String TVOC_ObsProperty="TVOC";
    public static final String TVOC_ObsResultName="总挥发性有机物";
    public static final String  TVOC_ObsResultUom="PPM";
}
