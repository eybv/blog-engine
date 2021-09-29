package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.BadRequestException;

public class IllegalOperationException extends BadRequestException {

    public IllegalOperationException() {
        super();
    }

    public IllegalOperationException(String message) {
        super(message);
    }

    public IllegalOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
