package com.cssd.ehealthcompanionapp.dtos;

import com.cssd.ehealthcompanionapp.database.support.UniqueKey;

import java.util.Date;

public class BloodPressure extends Measurement {

    private int systolicValue;
    private int diastolicValue;

    public BloodPressure() {}

    public BloodPressure(String patientId, int systolic, int diatolic) {
        this.patientId = patientId;
        this.systolicValue =  systolic;
        this.diastolicValue = diatolic;
        this.timestamp = new Date();
        this.type = "bloodPressure";
    }

    public int getSystolicValue() {
        return systolicValue;
    }

    public void setSystolicValue(int systolicValue) {
        this.systolicValue = systolicValue;
    }

    public int getDiastolicValue() {
        return diastolicValue;
    }

    public void setDiastolicValue(int diastolicValue) {
        this.diastolicValue = diastolicValue;
    }
}
