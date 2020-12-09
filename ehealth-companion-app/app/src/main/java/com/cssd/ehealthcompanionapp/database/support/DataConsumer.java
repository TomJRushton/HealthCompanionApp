package com.cssd.ehealthcompanionapp.database.support;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class DataConsumer<T> implements ValueEventListener {
    protected DataConsumer(Class<T> clazz, Consumer<T> success, Consumer<DatabaseError> fail) {
        this.success = success;
        this.fail = fail;
        this.clazz = clazz;
    }

    Consumer<T> success = null;
    Consumer<DatabaseError> fail = null;
    Consumer<Stream<T>> successList = null;
    Class<T> clazz;

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                if (successList != null) {
                    successList.accept(stream(dataSnapshot.getChildren().spliterator(), false).map(child -> child.getValue(clazz)));
                } else {
                    T data = dataSnapshot.getValue(clazz);
                    if (success != null)
                        success.accept(data);
                }
            } else {
                if (successList != null) {
                    successList.accept(Stream.empty());
                } else {
                    fail.accept(DatabaseError.fromException(new Exception("No data results")));
                }
            }
        } catch (Exception e) {
            if (fail != null)
                fail.accept(DatabaseError.fromException(e));
            else
                throw e;
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        if (fail != null)
            fail.accept(databaseError);
    }
}