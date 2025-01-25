package com.Tran.parser.AST;

import java.util.List;
import java.util.Optional;

public class MemberNode implements Node {
    public boolean isShared = false;
    public VariableDeclarationNode declaration;
    public Optional<List<StatementNode>> accessor = Optional.empty();
    public Optional<List<StatementNode>> mutator = Optional.empty();


    @Override
    public String toString() {
        return  declaration +
                (accessor.map(statementNodes -> "\naccessor:\n" + Node.statementListToString(statementNodes)).orElse("")) +
                (mutator.map(statementNodes -> "\nmutator:\n" + Node.statementListToString(statementNodes)).orElse("")) +
                "\n" ;
    }
}
