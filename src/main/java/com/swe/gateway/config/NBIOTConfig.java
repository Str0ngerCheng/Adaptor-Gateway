package com.swe.gateway.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cbw
 */
public class NBIOTConfig {

    public static final String Starting_Address = "0";
    public static final String Slave_Address = "1";
    public static final int Socket_TimeOut = 200000;
    public static final String SOS_Url = "http://gsw.whu.edu.cn:8080/MongoSOS/sos";

    public static final String Platform_ID="urn:ogc:feature:insitusensor:platform:Node001";
    public static final double Latitude=30.46921;
    public static final double Longitude=114.35593;

    public static final String []SensorID_SoilTemperatureHumidity=new String[]{
            "urn:ogc:feature:insitesensor:Node001-NBIoT-01",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-02",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-03",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-04",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-05",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-06",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-07",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-08",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-09",
        "urn:ogc:feature:insitesensor:Node001-NBIoT-10"};
 //   public static final String SensorID_SoilTemperatureHumidity11="urn:ogc:feature:insitesensor:Node001-NBIoT-11";

//    public static final String []deviceID=new String[]{
//            "001800383337363418473930",
//            "002400373337363416473930",
//            "002900393337363416473930",
//            "001400263337363417473930",
//            "004C00243337363416473930",
//            "003F004C3337363416473930",
//            "002D00623337363416473930",
//            "0038001B3337363418473930",
//            "003000593337363416473930"};

    public static final Map<String,String> deviceID_SOSID=new HashMap<String, String>(){{
        put("001800383337363418473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-01");
        put("002400373337363416473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-02");
        put("002900393337363416473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-03");
        put("001400263337363417473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-04");
        put("004C00243337363416473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-05");
        put("003F004C3337363416473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-06");
        put("002D00623337363416473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-07");
        put("0038001B3337363418473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-08");
        put("003000593337363416473930", "urn:ogc:feature:insitesensor:Node001-NBIoT-09");
    }};



    public static final String SoilTemperature_ObsProperty="SoilTemperature";
    public static final String SoilTemperature_ObsResultName="土壤温度";
    public static final String SoilTemperature_ObsResultUom="℃";

    public static final String SoilHumidity_ObsProperty="SoilHumidity";
    public static final String SoilHumidity_ObsResultName="土壤湿度";
    public static final String SoilHumidity_ObsResultUom="%";

//public static void main(String [] args){
//    long time1 = 1627767665;
//    String result1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time1 * 1000));
//    System.out.println("10位数的时间戳（秒）--->Date:" + result1);
//}
}
