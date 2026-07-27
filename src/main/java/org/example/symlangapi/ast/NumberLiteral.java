package org.example.symlangapi.ast;

public class NumberLiteral extends Expression {

    private final String value;

    public NumberLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}