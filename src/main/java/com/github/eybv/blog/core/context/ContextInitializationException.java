package com.github.eybv.blog.core.context;

public class ContextInitializationException extends RuntimeException {

    public ContextInitializationException() {
        super();
    }

    public ContextInitializationException(String message) {
        super(message);
    }

    public ContextInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextInitializationException(Throwable cause) {
        super(cause);
    }

}
