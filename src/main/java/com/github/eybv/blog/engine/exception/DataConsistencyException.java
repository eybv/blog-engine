package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.BadRequestException;

public class DataConsistencyException extends BadRequestException {

    public DataConsistencyException() {
        super();
    }

    public DataConsistencyException(String message) {
        super(message);
    }

    public DataConsistencyException(String message, Throwable cause) {
        super(message, cause);
    }

}
