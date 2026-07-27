package org.example.symlangapi.ast;

public class Assignment extends Statement {

    private final String name;
    private final Expression value;

    public Assignment(String name, Expression value) {
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