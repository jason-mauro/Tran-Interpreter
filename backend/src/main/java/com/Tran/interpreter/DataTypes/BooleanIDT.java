package com.Tran.interpreter.DataTypes;

public class BooleanIDT implements InterpreterDataType {
    public boolean Value;
    public boolean isInitialized = false;


    public BooleanIDT(boolean value) {
        this.Value = value;
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
        if (in instanceof BooleanIDT inv) {
            Value = inv.Value;
            isInitialized = true;
        }
        else {
            throw new RuntimeException("Trying to assign to a boolean IDT from a " + in.getClass());
        }
    }

    @Override
    public String toString() {
        return Value?"true":"false";
    }
}
