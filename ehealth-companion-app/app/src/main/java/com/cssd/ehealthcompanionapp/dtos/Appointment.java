package com.cssd.ehealthcompanionapp.dtos;

import com.cssd.ehealthcompanionapp.database.support.UniqueKey;

public class Appointment implements UniqueKey {
    private String uid;
    private String patientId;
    private String message;
    private long time;
    private String from;

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String getUid() {
        return null;
    }

    @Override
    public void setUid(String uid) {

    }
}
