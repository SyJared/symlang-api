package org.example.symlangapi.interpreter;

// Not a real error — a signal used to unwind out of nested if/while blocks
// back up to wherever the function was called from.
public class ReturnException extends RuntimeException {

    private final Object value;

    public ReturnException(Object value) {
        super(null, null, false, false); // skip stack trace capture, this isn't a real error
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}