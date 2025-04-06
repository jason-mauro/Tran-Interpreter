package com.Tran.parser.AST;


public class MemberExpressionNode implements Node, ExpressionNode {
    public ExpressionNode object; // Variable Reference
    public ExpressionNode property; // Caller or

    public MemberExpressionNode(ExpressionNode object, ExpressionNode property) {
        this.object = object;
        this.property = property;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(object.toString());
        sb.append(".");
        sb.append(property.toString());
        return sb.toString();
    }
}
