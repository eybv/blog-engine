package com.github.eybv.blog.core.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Parameter;

public interface ArgumentResolver {

    boolean canResolve(Parameter parameter);

    Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response);

}
