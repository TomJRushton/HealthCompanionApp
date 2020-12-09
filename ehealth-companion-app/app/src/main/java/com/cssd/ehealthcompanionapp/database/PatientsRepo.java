package com.cssd.ehealthcompanionapp.database;

import com.cssd.ehealthcompanionapp.database.support.Repo;
import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.google.firebase.database.FirebaseDatabase;

public class PatientsRepo extends Repo<GenericAccount> {

    private static PatientsRepo self;

    synchronized public static PatientsRepo getInstance() {
        if(self == null);
        self = new PatientsRepo();
        return self;
    }

    private PatientsRepo() {
        super(FirebaseDatabase.getInstance().getReference().getRoot().child("patients"), GenericAccount.class);
    }
}
