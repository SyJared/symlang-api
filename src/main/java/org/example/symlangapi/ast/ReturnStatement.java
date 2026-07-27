package org.example.symlangapi.ast;

public class ReturnStatement extends Statement {

    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
}