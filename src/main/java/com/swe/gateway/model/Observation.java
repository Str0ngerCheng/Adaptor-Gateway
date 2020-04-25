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
 * @since 2020-04-25
 */
public class Observation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "observation_id", type = IdType.AUTO)
    private Integer observationId;
    @TableField("sensor_id")
    private Integer sensorId;
    @TableField("obs_prop_id")
    private Integer obsPropId;
    @TableField("day")
    private Integer day;
    @TableField("hour")
    private Integer hour;
    @TableField("timestamp")
    private Integer timestamp;
    @TableField("obs_value")
    private String obsValue;


    public Integer getObservationId() {
        return observationId;
    }

    public void setObservationId(Integer observationId) {
        this.observationId = observationId;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public Integer getObsPropId() {
        return obsPropId;
    }

    public void setObsPropId(Integer obsPropId) {
        this.obsPropId = obsPropId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getObsValue() {
        return obsValue;
    }

    public void setObsValue(String obsValue) {
        this.obsValue = obsValue;
    }

    @Override
    public String toString() {
        return "Observation{" +
        "observationId=" + observationId +
        ", sensorId=" + sensorId +
        ", obsPropId=" + obsPropId +
        ", day=" + day +
        ", hour=" + hour +
        ", timestamp=" + timestamp +
        ", obsValue=" + obsValue +
        "}";
    }
}
