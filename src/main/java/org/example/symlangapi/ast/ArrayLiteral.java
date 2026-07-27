package org.example.symlangapi.ast;

import java.util.List;

public class ArrayLiteral extends Expression {

    private final List<Expression> elements;

    public ArrayLiteral(List<Expression> elements) {
        this.elements = elements;
    }

    public List<Expression> getElements() {
        return elements;
    }
}