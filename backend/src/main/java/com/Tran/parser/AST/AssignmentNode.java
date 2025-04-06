package com.Tran.parser.AST;

public class AssignmentNode implements StatementNode {
    public ExpressionNode target;
    public ExpressionNode expression;

    @Override
    public String toString() {
        return  target + " = " + expression + "\n";
    }

}
