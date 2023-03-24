package com.extentia.imagetotext.model;

import com.google.gson.annotations.SerializedName;

public class ImageScanRequest {

    @SerializedName("min_angle")
    private String minAngle;

    @SerializedName("max_angle")
    private String maxAngle;

    @SerializedName("min_value")
    private String minValue;

    @SerializedName("max_value")
    private String maxValue;

    public String getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(String minAngle) {
        this.minAngle = minAngle;
    }

    public String getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(String maxAngle) {
        this.maxAngle = maxAngle;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @SerializedName("units")
    private String units;

}
