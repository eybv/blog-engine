package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.BadRequestException;

public class PasswordResetException extends BadRequestException {

    public PasswordResetException() {
        super();
    }

    public PasswordResetException(String message) {
        super(message);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }

}
