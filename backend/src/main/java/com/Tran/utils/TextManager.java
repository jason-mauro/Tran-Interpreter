package com.Tran.utils;

public class TextManager {
    private final String text;
    private int position;
    private String fileName;

    public TextManager(String input) {
        this.text = input;
        this.position = 0;
        this.fileName = "";
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isAtEnd() {
        return position >= text.length();
    }

    public char peekCharacter() {
        return text.charAt(position);
    }

    public char peekCharacter(int distance) {
        return text.charAt(position + distance);
    }

    public char getCharacter() {
        return text.charAt(position++);
    }
}