package com.Tran.interpreter.DataTypes;

public class NumberIDT implements InterpreterDataType {
    public float Value;
    public boolean isInitialized = false;

    public NumberIDT(float v) {
        Value = v;
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
        if (in instanceof NumberIDT inv) {
            Value = inv.Value;
            isInitialized = true;
        } else {
            throw new RuntimeException("Trying to assign to a number IDT from a " + in.getClass());
        }
    }

    public String toString() {
        return String.valueOf(Value);
    }
}
