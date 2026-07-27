package org.example.symlangapi.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> variables = new HashMap<>();
    private final Environment parent; // null for the global scope

    public Environment() {
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public void declare(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.get(name);
        }
        throw new RuntimeException("Undefined variable: " + name);
    }

    public void assign(String name, Object value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
            return;
        }
        if (parent != null) {
            parent.assign(name, value);
            return;
        }
        throw new RuntimeException("Undefined variable: " + name);
    }

    // Read-only copy of this scope's variables — used later to send final
    // program state back to the frontend as JSON.
    public Map<String, Object> snapshot() {
        return new HashMap<>(variables);
    }

    @Override
    public String toString() {
        return variables.toString();
    }
}