package com.Tran.interpreter.DataTypes;

import com.Tran.parser.AST.ClassNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ReferenceIDT implements InterpreterDataType{
    public Optional<ObjectIDT> refersTo;
    public boolean isInitialized = false;
    public List<ClassNode> validAstNodes = new LinkedList<>();


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
        if (in instanceof ReferenceIDT inv) {
            if (!validAstNodes.contains(((ReferenceIDT) in).refersTo.get().astNode)){
                throw new RuntimeException("Trying to assign a reference IDT of a different type to this Object");
            }
            refersTo = inv.refersTo;
            isInitialized = true;
        } else if (in instanceof ObjectIDT obj) {
            if (!validAstNodes.contains(((ObjectIDT) in).astNode)){
                throw new RuntimeException("Trying to assign a reference IDT of a different type to this Object");
            }
            refersTo = Optional.of(obj);
            isInitialized = true;
        } else {
            throw new RuntimeException("Trying to assign to a reference IDT from a " + in.getClass());
        }
    }

    @Override
    public String toString() {
        if (refersTo.isPresent()) {
            return refersTo.get().toString();
        }
        return "<<<NULL REFERENCE>>>";
    }

}
