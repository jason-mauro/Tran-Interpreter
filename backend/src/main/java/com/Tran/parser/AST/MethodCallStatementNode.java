package com.Tran.parser.AST;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

// This is used in the case of a method call NOT used as an expression.
// This allows for multiple return values.
public class MethodCallStatementNode implements StatementNode {
    public MethodCallStatementNode() {
        returnValues = new LinkedList<>();
    }

    public MethodCallStatementNode(MethodCallExpressionNode mce) {
        returnValues = new LinkedList<>();
        object = mce.object;
        methodName = mce.methodName;
        parameters = mce.parameters;
    }

    public Optional<ExpressionNode> object;
    public String methodName; // Method calls
    public List<ExpressionNode> returnValues = new ArrayList<>();
    public List<ExpressionNode> parameters = new ArrayList<>();
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var r : returnValues)
            sb.append(r.toString()).append(",");
        return
                sb.toString() + (returnValues.isEmpty() ? "" : " = ") + object.map(s -> s + ".").orElse("") +
                methodName + " (" + Node.parameterListToString(parameters) + ")" ;
    }
}
