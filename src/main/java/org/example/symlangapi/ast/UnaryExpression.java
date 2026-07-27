package org.example.symlangapi.ast;

import org.example.symlangapi.lexer.TokenType;

public class UnaryExpression extends Expression {

    private final TokenType operator;
    private final Expression operand;

    public UnaryExpression(TokenType operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }
}