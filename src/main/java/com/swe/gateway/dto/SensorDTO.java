package com.swe.gateway.dto;

import com.swe.gateway.model.Sensor;

import java.io.Serializable;

public class SensorDTO implements Serializable , Comparable<SensorDTO> {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String location;
    private Integer status;
    private String protocol;
    private String type;
    public SensorDTO(Sensor sensor, String type){
        this.id=sensor.getSensorId();
        this.name=sensor.getSensorName();
        this.location=sensor.getLocation().replaceFirst(","," ");
        this.status=sensor.getStatus();
        this.protocol=sensor.getProtocol();
        this.type=type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int compareTo(SensorDTO o) {
        int value=this.name.compareTo(o.name);
        return value==0?this.type.compareTo(o.type):value;
    }
}
