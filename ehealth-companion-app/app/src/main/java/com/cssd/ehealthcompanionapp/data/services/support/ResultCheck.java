package com.cssd.ehealthcompanionapp.data.services.support;

public interface ResultCheck<T> {
    T get();
    boolean isSuccess();
    boolean isFail();
    boolean hasException();
    Exception getException();
    String getMessage();

}
