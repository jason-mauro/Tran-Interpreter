package com.Tran.parser.AST;

public class CharLiteralNode implements ExpressionNode {
    public char value;
    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
