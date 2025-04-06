package com.Tran.interpreter.DataTypes;

public interface InterpreterDataType {
    public void Assign(InterpreterDataType in);
    public boolean isInitialized();
    public void Initialize();
}

