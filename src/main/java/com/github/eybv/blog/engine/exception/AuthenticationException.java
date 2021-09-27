package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.ForbiddenException;

public class AuthenticationException extends ForbiddenException {

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
