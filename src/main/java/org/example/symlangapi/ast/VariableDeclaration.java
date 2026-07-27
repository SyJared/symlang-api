package org.example.symlangapi.ast;

public class VariableDeclaration extends Statement {

    private final String name;
    private final Expression value;

    public VariableDeclaration(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }
}