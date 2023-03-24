package com.extentia.imagetotext.model;

import java.io.Serializable;

public class ImageConfig implements Serializable {

    private String maxAngle;

    private String minAngle;

    private String maxValue;

    private String minValue;

    public String getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(String maxAngle) {
        this.maxAngle = maxAngle;
    }

    public String getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(String minAngle) {
        this.minAngle = minAngle;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String unit;

}
