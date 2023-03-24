package com.extentia.imagetotext.model;

import com.google.gson.annotations.SerializedName;

public class ScanResponse {

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    @SerializedName("reading")
    private String reading;
}
