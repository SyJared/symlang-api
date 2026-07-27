package org.example.symlangapi.ast;

public class IndexAssignment extends Statement {

    private final String arrayName;
    private final Expression index;
    private final Expression value;

    public IndexAssignment(String arrayName, Expression index, Expression value) {
        this.arrayName = arrayName;
        this.index = index;
        this.value = value;
    }

    public String getArrayName() {
        return arrayName;
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getValue() {
        return value;
    }
}