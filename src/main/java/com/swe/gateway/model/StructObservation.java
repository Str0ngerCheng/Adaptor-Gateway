package com.swe.gateway.model;

/**
 * @author cbw
 */
public class StructObservation {
    private String observedProperty;
    private String observedResultName;
    private String uom;
    private double resultValue;

    public StructObservation(String observedProperty, String observedResultName, String uom, double resultValue)
    {
        this.observedProperty = observedProperty;
        this.observedResultName = observedResultName;
        this.uom = uom;
        this.resultValue = resultValue;
    }

    public String getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public String getObservedResultName() {
        return observedResultName;
    }

    public void setObservedResultName(String observedResultName) {
        this.observedResultName = observedResultName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public double getResultValue() {
        return resultValue;
    }

    public void setResultValue(double resultValue) {
        this.resultValue = resultValue;
    }

}