package com.swe.gateway.model;

import java.io.Serializable;

/**
 * @author lx
 */
public class NBIOT implements Serializable {
    public NBIOT(String  deviceID, double temperature, double humidity, int timestamp) {
        this.deviceID = deviceID;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    private String deviceID;
private double temperature;

    @Override
    public String toString() {
        return "NBIOT{" +
                "deviceID='" + deviceID + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                '}';
    }

    private double humidity;
private int timestamp;
}
