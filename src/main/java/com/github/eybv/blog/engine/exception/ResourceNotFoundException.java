package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.BadRequestException;

public class ResourceNotFoundException extends BadRequestException {

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
