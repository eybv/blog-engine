package com.github.eybv.blog.core.resolver;

public class UnsupportedParameterException extends ArgumentResolverException {

    public UnsupportedParameterException() {
        super();
    }

    public UnsupportedParameterException(String message) {
        super(message);
    }

    public UnsupportedParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedParameterException(Throwable cause) {
        super(cause);
    }

}
