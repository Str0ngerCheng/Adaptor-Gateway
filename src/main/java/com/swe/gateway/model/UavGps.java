package com.swe.gateway.model;
/**
 * @author ljy
 */

import java.util.Date;

/**
 *无人机gps数据包实体类
 */
public class UavGps {
    private String mType="gps";
    /**
     * 数据包序号
     */
    private Long id;

    /**
     * 无人机编号
     */
    private String uavId;

    /**
     * 经纬度高程
     */
    private double lat;
    private double lon;
    private double alt;
    /**
     * 数据接收时间
     */
    private Date receivingTime;

    public UavGps(){}

    public UavGps(Long id, String uavId, double lat, double lon, double alt, Date receivingTime) {
        this.id = id;
        this.uavId = uavId;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.receivingTime = receivingTime;
    }

    public Long getId() {
        return id;
    }

    public String getUavId() {
        return uavId;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return alt;
    }

    public Date getReceivingTime() {
        return receivingTime;
    }

    public void setUavId(String uavId) {
        this.uavId = uavId;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public void setReceivingTime(Date receivingTime) {
        this.receivingTime = receivingTime;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
