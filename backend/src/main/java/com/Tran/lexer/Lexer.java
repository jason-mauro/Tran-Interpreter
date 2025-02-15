package com.Tran.lexer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.Tran.utils.*;

public class Lexer {
    private int columnNumber;
    private int lineNumber;
    private final TextManager textManager;
    private final HashMap<String, Token.TokenTypes> knownKeywords = new HashMap<>(){{
        put("accessor", Token.TokenTypes.ACCESSOR);
        put("mutator", Token.TokenTypes.MUTATOR);
        put("implements", Token.TokenTypes.IMPLEMENTS);
        put("class", Token.TokenTypes.CLASS);
        put("interface", Token.TokenTypes.INTERFACE);
        put("loop", Token.TokenTypes.LOOP);
        put("if", Token.TokenTypes.IF);
        put("else", Token.TokenTypes.ELSE);
        // Now used with boolean.true or boolean.false
//        put("true", Token.TokenTypes.TRUE);
//        put("false", Token.TokenTypes.FALSE);
        put("new", Token.TokenTypes.NEW);
        put("private", Token.TokenTypes.PRIVATE);
        put("shared", Token.TokenTypes.SHARED);
        put("construct", Token.TokenTypes.CONSTRUCT);
        put("null", Token.TokenTypes.NULL);
    }};
    private final HashMap<String, Token.TokenTypes> knownSymbols = new HashMap<>(){{
        put("=", Token.TokenTypes.ASSIGN);
        put("(", Token.TokenTypes.LPAREN);
        put(")", Token.TokenTypes.RPAREN);
        put(":", Token.TokenTypes.COLON);
        put(".", Token.TokenTypes.DOT);
        put("+", Token.TokenTypes.PLUS);
        put("-", Token.TokenTypes.MINUS);
        put("*", Token.TokenTypes.TIMES);
        put("/", Token.TokenTypes.DIVIDE);
        put("%", Token.TokenTypes.MODULO);
        put(",", Token.TokenTypes.COMMA);
        put("==", Token.TokenTypes.EQUAL);
        put("!=", Token.TokenTypes.NOTEQUAL);
        put("<", Token.TokenTypes.LESSTHAN);
        put(">", Token.TokenTypes.GREATERTHAN);
        put("<=", Token.TokenTypes.LESSTHANEQUAL);
        put(">=", Token.TokenTypes.GREATERTHANEQUAL);
        // Booleans are not with built-in objects
//        put("and", Token.TokenTypes.AND);
//        put("or", Token.TokenTypes.OR);
//        put("not", Token.TokenTypes.NOT);
    }};

    public Lexer(String input) {
        textManager = new TextManager(input);
        // Column and Line numbers are 1 indexed
        this.columnNumber = 0; // Incremented after every character call besides on newlines where it is set to 0
        this.lineNumber = 1; // Incremented after every newline
    }

    public List<Token> Lex() throws SyntaxErrorException {
        var retVal = new LinkedList<Token>();
        int currentIndentLevel = 0;
        while(!textManager.isAtEnd()) {
            char peekedCharacter = textManager.peekCharacter();
            if (peekedCharacter == '\n') {
                textManager.getCharacter();
                columnNumber++;
                retVal.add(new Token(Token.TokenTypes.NEWLINE, textManager.getFileName(), lineNumber, columnNumber));
                lineNumber++;
                columnNumber = 0;
            } else if ((peekedCharacter == '\r' && textManager.peekCharacter(1) == '\n')) {
                textManager.getCharacter();
                textManager.getCharacter();
                columnNumber+= 2;
                retVal.add(new Token(Token.TokenTypes.NEWLINE, textManager.getFileName(), lineNumber, columnNumber));
                lineNumber++;
                columnNumber = 0;
            } else if (columnNumber == 0 && (peekedCharacter == ' ' || peekedCharacter == '\t' || currentIndentLevel > 0)) { // Check the indentation level of a line
                // Count the spaces before a character appears
                int spaces = 0;
                while (!textManager.isAtEnd() && (textManager.peekCharacter() == '\t' || textManager.peekCharacter() == ' ')) {
                    if (textManager.peekCharacter() == '\t') {
                        spaces += 4;
                    } else {
                        spaces++;
                    }
                    textManager.getCharacter();
                    columnNumber++;
                }
                // Handle Comments on new lines as indentation for them does not matter
                if (!textManager.isAtEnd() && textManager.peekCharacter() == '{') {
                    parseComment();
                } else if (!textManager.isAtEnd() && textManager.peekCharacter() != '\n' && textManager.peekCharacter() != '\r') { // Check if the line is not empty and update the indent level
                    if (spaces % 4 != 0) { // Invalid # of spaces
                        throw new SyntaxErrorException("Indentation Error", lineNumber, columnNumber, textManager.getFileName());
                    } else { // Compare new indent level to the current one and create the correct amount of indent / dedent tokens
                        int newIndentLevel = spaces / 4;
                        if (newIndentLevel > currentIndentLevel) {
                            for (int i = 0; i < newIndentLevel - currentIndentLevel; i++) {
                                retVal.add(new Token(Token.TokenTypes.INDENT, textManager.getFileName(), lineNumber, columnNumber));
                            }
                        } else if (newIndentLevel < currentIndentLevel) {
                            for (int i = 0; i < currentIndentLevel - newIndentLevel; i++) {
                                retVal.add(new Token(Token.TokenTypes.DEDENT, textManager.getFileName(), lineNumber, columnNumber));
                            }
                        }
                        currentIndentLevel = newIndentLevel;
                    }
                }
            } else if (Character.isLetter(peekedCharacter)) {
                retVal.add(parseWord());
            } else if (Character.isDigit(peekedCharacter)) {
                retVal.add(parseNumber());
            } else if (peekedCharacter == '{'){
                parseComment();
            } else if (peekedCharacter == '.') {
                if (Character.isDigit(textManager.peekCharacter(1))) {
                    retVal.add(parseNumber());
                } else {
                    retVal.add(parsePunctuation());
                }
            } else if (peekedCharacter == '\'') {
                retVal.add(parseQuotedCharacter());
            } else if (peekedCharacter == '\"'){
                retVal.add(parseQuotedString());
            } else if (peekedCharacter != ' ' && peekedCharacter != '\t' && peekedCharacter != '\r'){ // Not whitespace
                var punctuation = parsePunctuation();
                if (punctuation != null) {
                    retVal.add(punctuation);
                }
            } else { // Skip any Whitespace
                textManager.getCharacter();
                columnNumber++;
            }
        }
        // Add dedent tokens at end of file if the current indent level is not 0
        for (int i = 0; i < currentIndentLevel; i++) {
            retVal.add(new Token(Token.TokenTypes.DEDENT, textManager.getFileName(), lineNumber, columnNumber));
        }
        return retVal;
    }

    private Token parseWord() throws SyntaxErrorException {
        StringBuilder wordBuffer = new StringBuilder(Character.toString(textManager.getCharacter()));
        columnNumber++;
        while(!textManager.isAtEnd()){
            char peekedCharacter = textManager.peekCharacter();
            // Allowed characters are Letters, Digits, and '_'
            if (!Character.isLetter(peekedCharacter) && !Character.isDigit(peekedCharacter) && peekedCharacter != '_') {
                return knownKeywords.containsKey(wordBuffer.toString()) ? new Token(knownKeywords.get(wordBuffer.toString()), textManager.getFileName(), lineNumber, columnNumber) : knownSymbols.containsKey(wordBuffer.toString()) ? new Token(knownSymbols.get(wordBuffer.toString()), textManager.getFileName(), lineNumber, columnNumber) : new Token(Token.TokenTypes.WORD, textManager.getFileName(), lineNumber, columnNumber, wordBuffer.toString());
            } else {
                wordBuffer.append(textManager.getCharacter());
                columnNumber++;
            }
        }
        return knownKeywords.containsKey(wordBuffer.toString()) ? new Token(knownKeywords.get(wordBuffer.toString()), textManager.getFileName(), lineNumber, columnNumber) : new Token(Token.TokenTypes.WORD, textManager.getFileName(), lineNumber, columnNumber, wordBuffer.toString());
    }

    private Token parseNumber() throws SyntaxErrorException {
        StringBuilder numberBuffer = new StringBuilder();
        boolean isDecimalPresent = false;
        while(!textManager.isAtEnd()) {
            char peekedCharacter = textManager.peekCharacter();
            if (!Character.isDigit(peekedCharacter) && peekedCharacter != '.') {
                if (Character.isLetter(peekedCharacter)){
                    throw new SyntaxErrorException("Invalid Number Format", lineNumber, columnNumber, textManager.getFileName());
                } else {
                    return new Token(Token.TokenTypes.NUMBER, textManager.getFileName(), lineNumber, columnNumber, numberBuffer.toString());
                }
            } else {
                // Check for a repeated decimal points
                if (peekedCharacter == '.'){
                    if (isDecimalPresent){
                        throw new SyntaxErrorException("Invalid Number Format", lineNumber, columnNumber, textManager.getFileName());
                    } else {
                        isDecimalPresent = true;
                    }
                }
                numberBuffer.append(textManager.getCharacter());
                columnNumber++;
            }
        }
        return new Token(Token.TokenTypes.NUMBER, textManager.getFileName(), lineNumber, columnNumber, numberBuffer.toString());
    }

    private Token parsePunctuation() throws SyntaxErrorException {
        String punctuation = Character.toString(textManager.getCharacter());

        columnNumber++;
        // Check for punctuation that is 2 characters long
        if (!textManager.isAtEnd() && knownSymbols.containsKey(punctuation + textManager.peekCharacter())){
            columnNumber++;
            return new Token(knownSymbols.get(punctuation + textManager.getCharacter()), textManager.getFileName(), lineNumber, columnNumber);
        } else if (knownSymbols.containsKey(punctuation)) {
            return new Token(knownSymbols.get(punctuation), textManager.getFileName(), lineNumber, columnNumber);
        } else { // Invalid Symbol
            if ((punctuation + "#").equals("##")){
                columnNumber = 0;
                lineNumber = 1;
                textManager.getCharacter();
                StringBuilder fileNameBuffer = new StringBuilder(Character.toString(textManager.getCharacter()));
                while(textManager.peekCharacter() != '#'){
                    fileNameBuffer.append(textManager.getCharacter());
                }
                textManager.setFileName(fileNameBuffer.toString());
                textManager.getCharacter();
                textManager.getCharacter();

                return null;
            } else {
                throw new SyntaxErrorException("Invalid Punctuation", lineNumber, columnNumber, textManager.getFileName());
            }
        }
    }

    private Token parseQuotedString() throws SyntaxErrorException {
        // Skip the Quote
        textManager.getCharacter();
        columnNumber++;
        StringBuilder wordBuffer = new StringBuilder();
        while(!textManager.isAtEnd() && textManager.peekCharacter() != '\"'){
            char peekedCharacter = textManager.peekCharacter();
            wordBuffer.append(textManager.getCharacter());
            if (peekedCharacter == '\n') {
                columnNumber = 0;
                lineNumber++;
            } else {
                columnNumber++;
            }
        }
        if (textManager.isAtEnd()){
            throw new SyntaxErrorException("Unclosed String", lineNumber, columnNumber, textManager.getFileName());
        }
        // Skip the closing quote
        textManager.getCharacter();
        columnNumber++;

        return new Token(Token.TokenTypes.QUOTEDSTRING, textManager.getFileName(), lineNumber, columnNumber, wordBuffer.toString());
    }

    private Token parseQuotedCharacter() throws SyntaxErrorException {
        textManager.getCharacter(); // Skip the single quote
        char character = textManager.getCharacter();
        columnNumber += 2;
        if (textManager.getCharacter() != '\'') {
            throw new SyntaxErrorException("Unclosed Character Literal", lineNumber, columnNumber, textManager.getFileName());
        }
        return new Token(Token.TokenTypes.QUOTEDCHARACTER, textManager.getFileName(), lineNumber, columnNumber, Character.toString(character));
    }

    private void parseComment() throws SyntaxErrorException {
        int depth = 1; // Keep track of the depth of left braces
        // Skip the {
        textManager.getCharacter();
        columnNumber++;
        while(!textManager.isAtEnd() && depth > 0){
            char currentCharacter = textManager.getCharacter();
            if (currentCharacter == '}'){
                depth--;
            } else if (currentCharacter == '{'){
                depth++;
            } else if (currentCharacter == '\n'){
                columnNumber = 0;
                lineNumber++;
            } else {
                columnNumber++;
            }
        }
        // Check for whitespace after a comment
        while(!textManager.isAtEnd() && (textManager.peekCharacter() == ' ' || textManager.peekCharacter() == '\t')){
            textManager.getCharacter();
            columnNumber++;
        }
        // Check for code after a comment
        if (!textManager.isAtEnd() && textManager.peekCharacter() != '\n' && textManager.peekCharacter() != '\r'){
            throw new SyntaxErrorException("Invalid Code after comment", lineNumber, columnNumber, textManager.getFileName());
        }
        if (depth > 0){
            throw new SyntaxErrorException("Unclosed Comment", lineNumber, columnNumber, textManager.getFileName());
        }
    }
}