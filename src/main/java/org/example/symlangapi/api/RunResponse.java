package org.example.symlangapi.api;

import java.util.List;
import java.util.Map;

public class RunResponse {

    private List<String> output;
    private Map<String, Object> variables;
    private String error;

    public RunResponse(List<String> output, Map<String, Object> variables, String error) {
        this.output = output;
        this.variables = variables;
        this.error = error;
    }

    public List<String> getOutput() {
        return output;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getError() {
        return error;
    }
}