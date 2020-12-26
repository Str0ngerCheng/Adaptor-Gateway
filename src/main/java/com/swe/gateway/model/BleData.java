package com.swe.gateway.model;

import java.io.Serializable;

public class BleData implements Serializable {
    private String amp; // 幅值
    private String pow; // 功率
    private String avg;//平均功率
    private String mas;//肌肉

    public String getAmp() {
        return amp;
    }

    public void setAmp(String amp) {
        this.amp = amp;
    }

    public String getPow() {
        return pow;
    }

    public void setPow(String pow) {
        this.pow = pow;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getMas() {
        return mas;
    }

    public void setMas(String mas) {
        this.mas = mas;
    }
}
