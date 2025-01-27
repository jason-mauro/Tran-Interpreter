package com.Tran.utils;

public class SyntaxErrorException extends Exception{
    private final int lineNumber, characterPosition;
    private final String message;
    private final String filename;

    public SyntaxErrorException(String message, int lineNumber, int characterPosition, String fileName) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
        this.filename = fileName;
    }

    @Override
    public String toString() {
        return "SyntaxErrorException in file " + "'" + filename + "': " + message + " at line " + lineNumber + ", character " + characterPosition;

    }
}