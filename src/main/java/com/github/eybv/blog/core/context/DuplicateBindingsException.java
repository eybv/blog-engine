package com.github.eybv.blog.core.context;

public class DuplicateBindingsException extends ContextInitializationException {

    public DuplicateBindingsException() {
        super();
    }

    public DuplicateBindingsException(String message) {
        super(message);
    }

    public DuplicateBindingsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateBindingsException(Throwable cause) {
        super(cause);
    }

}
