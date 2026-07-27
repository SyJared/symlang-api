package org.example.symlangapi.ast;

public class VariableReference extends Expression {

    private final String name;

    public VariableReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}