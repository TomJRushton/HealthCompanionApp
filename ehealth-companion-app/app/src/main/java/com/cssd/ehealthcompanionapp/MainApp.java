package com.cssd.ehealthcompanionapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainApp extends Application {

    private static FirebaseUser currentUser = null;

    public static void setFirebaseUser(FirebaseUser currentUser) {
        MainApp.currentUser = currentUser;
    }

    public static FirebaseUser getFirebaseUser(){
        return MainApp.currentUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(false);
    }

}
