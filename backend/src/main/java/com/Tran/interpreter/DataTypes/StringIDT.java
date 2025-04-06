package com.Tran.interpreter.DataTypes;

public class StringIDT implements InterpreterDataType {
    public String Value;
    public boolean isInitialized = false;

    public StringIDT(String s) {
        Value = s;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void Initialize(){
        isInitialized = true;
    }

    @Override
    public void Assign(InterpreterDataType in) {
        if (in instanceof StringIDT inv) {
            Value = inv.Value;
            isInitialized = true;
        } else {
            throw new RuntimeException("Trying to assign to a string IDT from a " + in.getClass());
        }
    }

    @Override
    public String toString() {
        return Value;
    }
}
