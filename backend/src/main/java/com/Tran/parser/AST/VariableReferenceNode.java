package com.Tran.parser.AST;

public class VariableReferenceNode implements Node, ExpressionNode {
    public String name;

    // This can be a list of variable names or methods
    // disambigute works just need to update this, VariableReference in the parser, findVariable, and findMethodCallAndRunIt() in interpreter and we are done
// should be able to have a methodcall expression inside this and a method call expression should contian a variablre reference node for each member that it is accessing so fix that
    @Override
    public String toString() {
        return name;
    }
}
