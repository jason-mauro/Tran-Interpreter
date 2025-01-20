package com.Tran.utils;

public class TextManager {
    private String text;
    private int position;

    public TextManager(String input) {
        this.text = input;
        this.position = 0;
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
