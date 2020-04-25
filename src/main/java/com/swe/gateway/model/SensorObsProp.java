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
public class SensorObsProp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "idsensor_obs_prop", type = IdType.AUTO)
    private Integer idsensorObsProp;
    @TableField("sensor_id")
    private Integer sensorId;
    @TableField("obs_prop_id")
    private Integer obsPropId;
    @TableField("dynamic_range")
    private String dynamicRange;
    @TableField("observation_accuracy")
    private String observationAccuracy;


    public Integer getIdsensorObsProp() {
        return idsensorObsProp;
    }

    public void setIdsensorObsProp(Integer idsensorObsProp) {
        this.idsensorObsProp = idsensorObsProp;
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

    public String getDynamicRange() {
        return dynamicRange;
    }

    public void setDynamicRange(String dynamicRange) {
        this.dynamicRange = dynamicRange;
    }

    public String getObservationAccuracy() {
        return observationAccuracy;
    }

    public void setObservationAccuracy(String observationAccuracy) {
        this.observationAccuracy = observationAccuracy;
    }

    @Override
    public String toString() {
        return "SensorObsProp{" +
        "idsensorObsProp=" + idsensorObsProp +
        ", sensorId=" + sensorId +
        ", obsPropId=" + obsPropId +
        ", dynamicRange=" + dynamicRange +
        ", observationAccuracy=" + observationAccuracy +
        "}";
    }
}
