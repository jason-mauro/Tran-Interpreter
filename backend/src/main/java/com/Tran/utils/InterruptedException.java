package com.Tran.utils;

public class InterruptedException extends RuntimeException {
    private final String message;

    public InterruptedException(String message) {
        this.message = message;

    }

    @Override
    public String toString() {
        return message;

    }
    @Override
    public String getMessage() {
        return message;
    }

}
