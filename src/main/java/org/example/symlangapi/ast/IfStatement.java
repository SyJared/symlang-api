package org.example.symlangapi.ast;

import java.util.List;

public class IfStatement extends Statement {

    private final Expression condition;
    private final List<Statement> thenBranch;
    private final List<Statement> elseBranch; // null if there's no else

    public IfStatement(Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getThenBranch() {
        return thenBranch;
    }

    public List<Statement> getElseBranch() {
        return elseBranch;
    }
}