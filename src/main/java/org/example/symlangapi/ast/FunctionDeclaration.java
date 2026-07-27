package org.example.symlangapi.ast;

import java.util.List;

public class FunctionDeclaration extends Statement {

    private final String name;
    private final List<String> parameters;
    private final List<Statement> body;

    public FunctionDeclaration(String name, List<String> parameters, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Statement> getBody() {
        return body;
    }
}