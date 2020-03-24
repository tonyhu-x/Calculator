package com.taj.calcengine;

public class SyntaxException extends RuntimeException {
    public SyntaxException() {
    }

    public SyntaxException(String message) {
        super("Syntax error at \"" + message + "\".");
    }
}
