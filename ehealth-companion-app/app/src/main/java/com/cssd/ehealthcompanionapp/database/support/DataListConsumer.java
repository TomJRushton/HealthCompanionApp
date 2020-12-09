package com.cssd.ehealthcompanionapp.database.support;

import com.google.firebase.database.DatabaseError;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class DataListConsumer<T> extends DataConsumer<T> {
    public DataListConsumer(Class<T> clazz, Consumer<Stream<T>> success, Consumer<DatabaseError> fail) {
        super(clazz, null, fail);
        this.successList = success;
    }
}