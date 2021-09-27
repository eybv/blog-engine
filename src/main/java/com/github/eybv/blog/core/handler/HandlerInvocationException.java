package com.github.eybv.blog.core.handler;

public class HandlerInvocationException extends HandlerException {

    public HandlerInvocationException() {
        super();
    }

    public HandlerInvocationException(String message) {
        super(message);
    }

    public HandlerInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandlerInvocationException(Throwable cause) {
        super(cause);
    }

}
