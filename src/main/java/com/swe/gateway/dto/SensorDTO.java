package com.swe.gateway.dto;

import com.swe.gateway.model.Sensor;

import java.io.Serializable;

public class SensorDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer sensorId;
    private String sensorName;
    private String location;
    private Integer status;
    private String protocol;
    private String type;
    public SensorDTO(Sensor sensor, String type){
        this.sensorId=sensor.getSensorId();
        this.sensorName=sensor.getSensorName();
        this.location=sensor.getLocation();
        this.status=sensor.getStatus();
        this.protocol=sensor.getProtocol();
        this.type=type;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
