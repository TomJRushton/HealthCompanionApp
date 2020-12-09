package com.cssd.ehealthcompanionapp.database.support;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class DataUpdateTransaction<T> implements Transaction.Handler {


    public DataUpdateTransaction(Class<T> clazz, UnaryOperator<T> update, Consumer<T> success, Consumer<DatabaseError> fail ) {
        this.update = update;
        this.clazz = clazz;
        this.fail = fail;
        this.success = success;
    }

    private UnaryOperator<T> update;
    private final Class<T> clazz;
    private Consumer<DatabaseError> fail;
    private Consumer<T> success;

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        T account = mutableData.getValue(clazz);
        if (account == null) {
            try {
                account = clazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return Transaction.abort();
            }
        } else {
            account = update.apply(account);
        }

        mutableData.setValue(account);
        return Transaction.success(mutableData);
    }

    @Override
    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
        // Transaction completed
        if (fail != null && !b && databaseError != null) {
            fail.accept(databaseError);
        } else if(success != null && b) {
            Object snapshotValue = dataSnapshot.getValue();
            if (snapshotValue != null) {
                T value = dataSnapshot.getValue(clazz);
                success.accept(value);
            }
            else
                fail.accept(DatabaseError.fromException(new ClassCastException("Data snapshot does not match")));
        } else {
            fail.accept(databaseError);
        }
    }
}
