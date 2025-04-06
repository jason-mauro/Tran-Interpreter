package com.Tran.interpreter.DataTypes;

public class CharIDT implements InterpreterDataType {
    public char Value;
    public boolean isInitialized = false;

    public CharIDT(char value) {
        Value = value;
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
        if (in instanceof CharIDT inv) {
            Value = inv.Value;
            isInitialized = true;
        } else {
            throw new RuntimeException("Trying to assign to a character IDT from a " + in.getClass());
        }
    }

    public String toString() {
        return String.valueOf(Value);
    }
}
