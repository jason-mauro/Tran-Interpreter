package com.Tran.parser.AST;

import com.Tran.interpreter.DataTypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MethodDeclarationNode implements Node {
    public boolean isShared;
    public boolean isPrivate;
    public String name;
    public List<VariableDeclarationNode> parameters = new ArrayList<>();
    public List<VariableDeclarationNode> returns = new ArrayList<>();
    public List<VariableDeclarationNode> locals = new ArrayList<>();
    public List<StatementNode> statements = new ArrayList<>();

    public HashMap<String, InterpreterDataType> localMap = new HashMap<>();

    @Override
    public String toString() {
        return
                (isShared ? "shared " : "") +
                (isPrivate ? "private " : "") +
                        name + " (" + Node.variableDeclarationListToString(parameters) + ")" +
                        (returns.isEmpty() ? "" : " : " + Node.variableDeclarationListToString(returns)) + "\n" +
                        locals.stream().map(Object::toString).collect(Collectors.joining("\n")) +
                        Node.statementListToString(statements) + "\n";
    }
}
