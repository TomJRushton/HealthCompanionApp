package com.cssd.ehealthcompanionapp.data.services.support;

import android.util.Log;

import com.google.firebase.database.DatabaseError;

import java.util.function.Consumer;

public class ServiceResult<T> implements ResultCheck<T> {
    private boolean success;
    private T result;
    private String message = "success";
    private Exception exception;
    private Consumer<ResultCheck<T>> andThen;
    private boolean accepted = false;

    public ServiceResult(Consumer<ResultCheck<T>> andThen) {
        this.andThen = andThen;
    }

    public void accept(T result) {
        if (!accepted) {
            accepted = true;
            this.result = result;
            success = true;
            this.andThen.accept(this);

        }
    }

    public void accept() {
        if (!accepted) {
            accepted = true;
            this.result = null;
            success = true;
            this.andThen.accept(this);

        }
    }

    public void fail(Exception e, String message) {
        if (!accepted) {
            accepted = true;
            this.result = null;
            this.message = message;
            success = false;
            this.exception = e;
            this.andThen.accept(this);

        }
    }

    public void fail(Exception e) {
        if (!accepted) {
            accepted = true;
            this.result = null;
            success = false;
            this.exception = e;
            this.andThen.accept(this);

        }
    }

    public void resolve(ResultCheck r) {
        if (!accepted) {
            accepted = true;
            this.result = null;
            if(r.isSuccess()) {
                success = true;
            } else {
                success = false;
                this.message = r.getMessage();
                this.exception = r.getException();
            }

            this.andThen.accept(this);
        }
    }

    public void fail(ResultCheck r) {
        if (!accepted && r.isFail()) {
            accepted = true;
            this.result = null;
            success = false;
            this.message = r.getMessage();
            this.exception = r.getException();
            this.andThen.accept(this);

        }
    }

    public void fail(String message) {
        if (!accepted) {
            accepted = true;
            this.result = null;
            this.message = message;
            success = false;
            this.andThen.accept(this);

        }
    }

    public void fail(DatabaseError error) {
        if (!accepted) {
            accepted = true;
            this.result = null;
            if (error != null) {
                this.message = error.getMessage();
                this.exception = error.toException();
            } else {
                this.message = "failed";
            }
            success = false;
            this.andThen.accept(this);

        }
    }

    public void fail(T result, String message) {
        if (!accepted) {
            accepted = true;
            this.result = result;
            this.message = message;
            success = false;
            this.andThen.accept(this);

        }
    }

    public static <T> void assertSuccess(ResultCheck<T> resultCheck) {
        if (resultCheck.isFail()){
            throw new RuntimeException(resultCheck.getException());
        }
    }

    public static <T> void logSuccess(ResultCheck<T> resultCheck) {
        if (resultCheck.isFail()){

            StringBuilder stack = new StringBuilder();
            stack.append("Location: ").append(System.lineSeparator());
            for (StackTraceElement traceElement : Thread.currentThread().getStackTrace()) {
                stack.append(traceElement.toString()).append(System.lineSeparator());
            }
            stack.append(System.lineSeparator()).append("message: ").append(resultCheck.getMessage());

            if (resultCheck.hasException())
                Log.e("ServiceResult", stack.toString(), resultCheck.getException());
            else
                Log.e("ServiceResult", stack.toString());
        }
    }

    @Override
    public T get() {
        return result;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean isFail() {
        return !success;
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
