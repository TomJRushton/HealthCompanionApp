package com.cssd.ehealthcompanionapp.dtos;

import java.util.Date;

public class HeartRate extends Measurement{

    private double min;
    private double max;
    private double average;

    public HeartRate() {}

    public HeartRate(String patientId, Date timestamp, double min, double max, double average) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.min = min;
        this.max = max;
        this.average = average;
        this.type = "heartRate";
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }


    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
