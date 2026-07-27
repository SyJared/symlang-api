package org.example.symlangapi.ast;

import org.example.symlangapi.lexer.TokenType;

public class BinaryExpression extends Expression {

    private final Expression left;
    private final TokenType operator;
    private final Expression right;

    public BinaryExpression(Expression left, TokenType operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public TokenType getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}