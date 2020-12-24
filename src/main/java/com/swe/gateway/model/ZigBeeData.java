package com.swe.gateway.model;

import java.io.Serializable;

/**
 * @author lrh
 */

public class ZigBeeData implements Serializable {
    private int obsId;
    private double obs;
    private String obsName;

    public String getObsName() {
        return obsName;
    }

    public void setObsName(String obsName) {
        this.obsName = obsName;
    }

    public int getObsId() {
        return obsId;
    }

    public void setObsId(int obsId) {
        this.obsId = obsId;
    }

    public double getObs() {
        return obs;
    }

    public void setObs(double obs) {
        this.obs = obs;
    }

    public ZigBeeData(int obsId, double obs,String obsName){
        this.obsId=obsId;
        this.obs=obs;
        this.obsName=obsName;
    }
    @Override
    public String toString() {
        return "NBIOT{" +
                "obsID='" + obsId + '\'' +
                ", obs=" + obs +
                ", obsName=" +obsName +
                '}';
    }
}
