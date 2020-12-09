package com.cssd.ehealthcompanionapp.dtos;

import com.cssd.ehealthcompanionapp.database.support.UniqueKey;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class GenericAccount implements UniqueKey {
    private String displayName;

    private String firstName;
    private String surname;
    private Date dob;
    private String ethnicity;
    private String gender;
    private int height;
    private int weight;
    private String email;
    private String photoUrl;
    private boolean anonymity;
    private String uid;
    private Date lastSynced = new Date();

    public GenericAccount(FirebaseUser firebaseUser) {
        uid = firebaseUser.getUid();
        displayName = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();
        if (firebaseUser.getPhotoUrl() != null)
            photoUrl = firebaseUser.getPhotoUrl().toString();
        anonymity = firebaseUser.isAnonymous();
    }

    public GenericAccount() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isAnonymity() {
        return anonymity;
    }

    public void setAnonymity(boolean anonymity) {
        this.anonymity = anonymity;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(Date lastSynced) {
        this.lastSynced = lastSynced;
    }
}
