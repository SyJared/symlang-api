package org.example.symlangapi.interpreter;

import org.example.symlangapi.ast.FunctionDeclaration;

public class FunctionValue {

    private final FunctionDeclaration declaration;
    private final Environment closure;

    public FunctionValue(FunctionDeclaration declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    public FunctionDeclaration getDeclaration() {
        return declaration;
    }

    public Environment getClosure() {
        return closure;
    }

    @Override
    public String toString() {
        return "<function " + declaration.getName() + "/" + declaration.getParameters().size() + ">";
    }
}