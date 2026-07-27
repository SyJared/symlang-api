package org.example.symlangapi.ast;

import org.example.symlangapi.lexer.TokenType;

public class ComparisonOperation extends Expression {

    private final Expression left;
    private final TokenType condition;
    private final Expression right;

    public ComparisonOperation(Expression left, TokenType condition, Expression right) {
        this.left = left;
        this.condition = condition;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public TokenType getCondition() {
        return condition;
    }

    public Expression getRight() {
        return right;
    }
}