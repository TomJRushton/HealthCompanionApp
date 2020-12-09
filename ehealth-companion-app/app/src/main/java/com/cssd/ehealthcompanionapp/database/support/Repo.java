package com.cssd.ehealthcompanionapp.database.support;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;


public abstract class Repo<T extends UniqueKey> {

    private final DatabaseReference repo;
    private final Class<T> clazz;

    public Repo(DatabaseReference repo, Class<T> clazz) {
        this.repo = repo;
        this.clazz = clazz;
    }

    public void add(final T object, @NonNull Consumer<T> success, @NonNull Consumer<Exception> fail) {
        DatabaseReference child = repo.push();
        child.setValue(updateKey(child.getKey()).apply(object))
                .addOnSuccessListener(successful -> success.accept(updateKey(child.getKey()).apply(object)))
                .addOnFailureListener(fail::accept);
    }

    public void remove(String uid, Consumer<Void> success, Consumer<Exception> fail) {
        repo.child(uid).removeValue((e,l)->{
            if (e!=null){
                fail.accept(e.toException());
            } else {
                success.accept(null);
            }
        });
    }

    public void remove(String uid, Consumer<Exception> fail) {
        repo.child(uid).removeValue().addOnFailureListener(fail::accept);
    }

    public void get(String uid,  Consumer<T> success) {
        get(uid, success, null);
    }

    public void get(String uid, Consumer<T> success, Consumer<DatabaseError> fail) {
        repo.child(uid).addListenerForSingleValueEvent(new DataConsumer<>(clazz, success, fail));
    }

    public void check(String uid, Consumer<Boolean> exists) {
        check(uid, exists, null);
    }

    public void check(String uid, Consumer<Boolean> exists, Consumer<DatabaseError> fail) {
        repo.child(uid).addListenerForSingleValueEvent(new DataChecker(exists, fail));
    }

    public void stream(Consumer<Stream<T>> success, Consumer<DatabaseError> fail) {
        repo.addListenerForSingleValueEvent(new DataListConsumer<>(clazz, success, fail));
    }

    public void stream(UnaryOperator<Query> query, Consumer<Stream<T>> success, Consumer<DatabaseError> fail) {
        query.apply(repo).addListenerForSingleValueEvent(new DataListConsumer<>(clazz, success, fail));
    }
    public void stream(Consumer<Stream<T>> success) {
        repo.addListenerForSingleValueEvent(new DataListConsumer<>(clazz, success, null));
    }

    public void set(String uid, T object, Consumer<T> success, Consumer<DatabaseError> fail) {
        repo.child(uid).setValue(updateKey(uid).apply(object))
                .addOnSuccessListener(s -> success.accept(updateKey(uid).apply(object)))
                .addOnFailureListener(f -> fail.accept(DatabaseError.fromException(f)));
    }

    public void update(String uid, UnaryOperator<T> update, Consumer<T> success, Consumer<DatabaseError> fail) {
        update.compose(updateKey(uid));
        repo.child(uid).runTransaction(new DataUpdateTransaction<>(clazz, update, success, fail));
    }

    private Function<T, T> updateKey(String uid) {
        return object -> {
            if (object != null) {
                object.setUid(uid);
            }
            return object;
        };
    }

}
