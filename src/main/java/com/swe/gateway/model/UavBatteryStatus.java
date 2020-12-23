package com.swe.gateway.model;
/**
 * @author ljy
 */

import io.dronefleet.mavlink.common.MavBatteryFunction;
import io.dronefleet.mavlink.common.MavBatteryType;
import io.dronefleet.mavlink.util.EnumValue;

import java.util.Date;
import java.util.List;

/**
 *无人机电池状态数据包实体类
 */
public class UavBatteryStatus {
    private String mType="batterystatus";
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
    private int batteryId;
    private int batteryFunction;
    private int batteryType;
    private int temperature;
    private int voltages;
    private int currentBattery;
    private int currentCconsumed;
    private int energyCconsumed ;
    private int batteryRemaining;
    /**
     * 数据接收时间
     */
    private Date receivingTime;

    public UavBatteryStatus(){};

    public UavBatteryStatus(Long id, String uavId, int batteryId, int batteryFunction, int batteryType, int temperature, int voltages, int currentBattery, int currentCconsumed, int energyCconsumed, int batteryRemaining, Date receivingTime) {
        this.id = id;
        this.uavId = uavId;
        this.batteryId = batteryId;
        this.batteryFunction = batteryFunction;
        this.batteryType = batteryType;
        this.temperature = temperature;
        this.voltages = voltages;
        this.currentBattery = currentBattery;
        this.currentCconsumed = currentCconsumed;
        this.energyCconsumed = energyCconsumed;
        this.batteryRemaining = batteryRemaining;
        this.receivingTime = receivingTime;
    }

    public Long getId() {
        return id;
    }

    public String getUavId() {
        return uavId;
    }

    public int getBatteryId() {
        return batteryId;
    }

    public int getBatteryFunction() {
        return batteryFunction;
    }

    public int getBatteryType() {
        return batteryType;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getVoltages() {
        return voltages;
    }

    public int getCurrentBattery() {
        return currentBattery;
    }

    public int getCurrentCconsumed() {
        return currentCconsumed;
    }

    public int getEnergyCconsumed() {
        return energyCconsumed;
    }

    public int getBatteryRemaining() {
        return batteryRemaining;
    }

    public Date getReceivingTime() {
        return receivingTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUavId(String uavId) {
        this.uavId = uavId;
    }

    public void setBatteryId(int batteryId) {
        this.batteryId = batteryId;
    }

    public void setBatteryFunction(int batteryFunction) {
        this.batteryFunction = batteryFunction;
    }

    public void setBatteryType(int batterytype) {
        this.batteryType = batterytype;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setVoltages(int voltages) {
        this.voltages = voltages;
    }

    public void setCurrentBattery(int currentBattery) {
        this.currentBattery = currentBattery;
    }

    public void setCurrentCconsumed(int currentCconsumed) {
        this.currentCconsumed = currentCconsumed;
    }

    public void setEnergyCconsumed(int energyCconsumed) {
        this.energyCconsumed = energyCconsumed;
    }

    public void setBatteryRemaining(int batteryRemaining) {
        this.batteryRemaining = batteryRemaining;
    }

    public void setReceivingTime(Date receivingTime) {
        this.receivingTime = receivingTime;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmType() {
        return mType;
    }
}
