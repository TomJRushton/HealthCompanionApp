package com.cssd.ehealthcompanionapp.dtos;

import java.util.Date;

public class StepCount extends Measurement {

    private int stepCount;

    public StepCount() {
    }

    public StepCount(String patientId, Date timestamp, int stepCount) {
        this.stepCount = stepCount;
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.type = "stepCount";
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
}
