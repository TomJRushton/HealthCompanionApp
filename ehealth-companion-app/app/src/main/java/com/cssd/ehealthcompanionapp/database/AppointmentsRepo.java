package com.cssd.ehealthcompanionapp.database;

import com.cssd.ehealthcompanionapp.database.support.Repo;
import com.cssd.ehealthcompanionapp.dtos.Appointment;
import com.google.firebase.database.FirebaseDatabase;

public class AppointmentsRepo extends Repo<Appointment> {
    private static AppointmentsRepo self;

    synchronized public static AppointmentsRepo getInstance() {
        if(self == null);
        self = new AppointmentsRepo();
        return self;
    }

    private AppointmentsRepo() {
        super(FirebaseDatabase.getInstance().getReference().getRoot().child("appointments"), Appointment.class);
    }
}
