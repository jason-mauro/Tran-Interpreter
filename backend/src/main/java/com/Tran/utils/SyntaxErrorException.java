package com.Tran.utils;

public class SyntaxErrorException extends Exception{
    private final int lineNumber, characterPosition;
    private final String message;

    public SyntaxErrorException(String message, int lineNumber, int characterPosition) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
    }

    @Override
    public String toString() {
        return "SyntaxErrorException: " + message + " at line " + lineNumber + ", character " + characterPosition;

    }

    public String getMessage() {
        return "SyntaxErrorException: " + message + " at line " + lineNumber + ", character " + characterPosition;
    }
}
