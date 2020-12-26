package com.swe.gateway.dto;

import com.swe.gateway.model.Observation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObservationDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sensorName;
    private String sensorType;
    private String uom;
    private List<SingleObservation> observations = new ArrayList<>();

    public ObservationDTO(String sensorName, String sensorType, String uom, List<Observation> obss) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.uom = uom;
        for (int i = 0; i < obss.size(); i++) {
            SingleObservation singleObservation = new SingleObservation(obss.get(i));
            this.observations.add(singleObservation);
        }
        Collections.sort(this.observations);

    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public List<SingleObservation> getObservations() {
        return observations;
    }

    public void setObservations(List<SingleObservation> observations) {
        this.observations = observations;
    }

    class SingleObservation implements Serializable, Comparable<SingleObservation> {
        private Integer day;
        private Integer hour;
        private String obsValue;

        public SingleObservation(Observation obs) {
            this.day = obs.getDay();
            this.hour = obs.getHour();
            this.obsValue = obs.getObsValue();
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

        public String getObsValue() {
            return obsValue;
        }

        public void setObsValue(String obsValue) {
            this.obsValue = obsValue;
        }

        @Override
        public int compareTo(SingleObservation o) {
            if (this.day > o.day) return 1;
            else if (this.day < o.day) return -1;
            else if (this.hour > o.hour) return 1;
            else if (this.hour < o.hour) return -1;
            else return 0;
        }
    }
}
