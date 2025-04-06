package com.Tran.interpreter.DataTypes;

import com.Tran.parser.AST.ClassNode;
import com.Tran.parser.AST.MethodDeclarationNode;

import java.util.HashMap;

public class ObjectIDT implements InterpreterDataType {
    public HashMap<String,InterpreterDataType> members = new HashMap<>();
    public final ClassNode astNode;
    public boolean isInitialized = false;
    public boolean isSharedMethodCall = false;


    public ObjectIDT(ClassNode astNode) {
        this.astNode = astNode;
    }

    @Override
    public void Initialize(){
        isInitialized = true;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void Assign(InterpreterDataType in) {
            throw new RuntimeException("Trying to assign to an object IDT from a " + in.getClass());
    }

    @Override
    public String toString() {
        if (astNode == null){
            return "null";
        }
        StringBuilder out = new StringBuilder();
        for (var m : members.entrySet())
            out.append(m.getKey()).append(" : ").append(m.getValue().toString()).append("\n");
        return out.toString();
    }
}
