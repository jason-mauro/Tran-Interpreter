package com.Tran.utils;

import java.util.List;
import java.util.Optional;


public class TokenManager {
    List<Token> tokens;

    public TokenManager(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean done() {
        return tokens.isEmpty();
    }

    public Optional<Token> matchAndRemove(Token.TokenTypes t) {
        if (!tokens.isEmpty()){
            Token compareToken = tokens.getFirst();
            if (compareToken.getType().equals(t)){
                tokens.removeFirst();
                return Optional.of(compareToken);
            }
        }
        return Optional.empty();
    }

    public Optional<Token> peek(int i) {
        return i >= 0 && tokens.size() > i  ? Optional.of(tokens.get(i)) : Optional.empty();
    }

    // Checks if there is 2 tokens in the list and returns if the next two tokens match
    public boolean nextTwoTokensMatch(Token.TokenTypes first, Token.TokenTypes second){
        return tokens.size() >= 2 && first.equals(tokens.getFirst().getType()) && second.equals(tokens.get(1).getType());
    }

    public String getCurrentFileName() {
        return tokens.getFirst().getFileName();
    }
    public int getCurrentLine(){
        return tokens.getFirst().getLineNumber();
    }

    public int getCurrentColumnNumber(){
        return tokens.getFirst().getColumnNumber();
    }
}