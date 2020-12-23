package com.swe.gateway.model;
/**
 * @author ljy
 */

import java.util.Date;

/**
 *无人机飞行数据包实体类
 */
public class UavVfrHud {
    private String mType="vfrhud";
    /**
     * 数据包序号
     */
    private Long id;

    /**
     * 无人机编号
     */
    private String uavId;

    /**
     * 飞行参数
     */
    private float airspeed;
    private float groundspeed;
    private int heading;
    private int throttle;
    private float altitude;
    private float climb;
    /**
     * 数据接收时间
     */
    private Date receivingTime;

    public UavVfrHud(){};

    public UavVfrHud(Long id, String uavId, float airspeed, float groundspeed, int heading, int throttle, float altitude, float climb, Date receivingTime) {
        this.id = id;
        this.uavId = uavId;
        this.airspeed = airspeed;
        this.groundspeed = groundspeed;
        this.heading = heading;
        this.throttle = throttle;
        this.altitude = altitude;
        this.climb = climb;
        this.receivingTime = receivingTime;
    }

    public Long getId() {
        return id;
    }

    public String getUavId() {
        return uavId;
    }

    public float getAirspeed() {
        return airspeed;
    }

    public float getGroundspeed() {
        return groundspeed;
    }

    public int getHeading() {
        return heading;
    }

    public int getThrottle() {
        return throttle;
    }

    public float getAltitude() {
        return altitude;
    }

    public float getClimb() {
        return climb;
    }

    public Date getReceivingTime() {
        return receivingTime;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUavId(String uavId) {
        this.uavId = uavId;
    }

    public void setAirspeed(float airspeed) {
        this.airspeed = airspeed;
    }

    public void setGroundspeed(float groundspeed) {
        this.groundspeed = groundspeed;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public void setClimb(float climb) {
        this.climb = climb;
    }

    public void setReceivingTime(Date receivingTime) {
        this.receivingTime = receivingTime;
    }
}
