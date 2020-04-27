package com.swe.gateway.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;


import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author cbw
 * @since 2020-04-24
 */

public class Sensor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sensor_id", type = IdType.AUTO)
    private Integer sensorId;
    @TableField("sensor_name")
    private String sensorName;
    @TableField("is_insitu")
    private Integer isInsitu;
    @TableField("location")
    private String location;
    @TableField("status")
    private Integer status;
    @TableField("protocol")
    private String protocol;
    @TableField("decription")
    private String decription;



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

    public Integer getIsInsitu() {
        return isInsitu;
    }

    public void setIsInsitu(Integer isInsitu) {
        this.isInsitu = isInsitu;
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

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorId=" + sensorId +
                ", sensorName=" + sensorName +
                ", isInsitu=" + isInsitu +
                ", location=" + location +
                ", status=" + status +
                ", protocol=" + protocol +
                ", decription=" + decription +
                "}";
    }
}
