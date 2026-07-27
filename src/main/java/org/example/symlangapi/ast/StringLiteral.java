package org.example.symlangapi.ast;

public class StringLiteral extends Expression {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}