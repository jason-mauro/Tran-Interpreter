package com.Tran.utils;

public class TranRuntimeException extends RuntimeException{

    private final String message;

    public TranRuntimeException(String message) {
        this.message = message;

    }

    @Override
    public String toString() {
        return "TranRuntimeException: " + message;

    }
    @Override
    public String getMessage() {
        return "TranRuntimeException: " + message;
    }

}