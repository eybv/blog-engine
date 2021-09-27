package com.github.eybv.blog.core.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface RequestHandler {

    void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
