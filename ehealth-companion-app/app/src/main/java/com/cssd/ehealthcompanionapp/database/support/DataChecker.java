package com.cssd.ehealthcompanionapp.database.support;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class DataChecker implements ValueEventListener {

    public DataChecker(Consumer<Boolean> result, Consumer<DatabaseError> fail) {
        this.result = result;
        this.fail = fail;
    }

    Consumer<Boolean> result = null;
    Consumer<DatabaseError> fail = null;

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (result != null) {
            result.accept(dataSnapshot.getValue() != null);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        if (fail != null)
            fail.accept(databaseError);
    }
}