package com.github.eybv.blog.core.error;

public class MethodNotAllowedException extends HttpException {

    public MethodNotAllowedException() {
        super(405, "Method Not Allowed", "");
    }

    public MethodNotAllowedException(String message) {
        super(405, "Method Not Allowed", message);
    }

    public MethodNotAllowedException(String message, Throwable cause) {
        super(405, "Method Not Allowed", message, cause);
    }

}
