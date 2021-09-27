package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.BadRequestException;

public class WrongCredentialsException extends BadRequestException {

    public WrongCredentialsException() {
        super();
    }

    public WrongCredentialsException(String message) {
        super(message);
    }

    public WrongCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

}
