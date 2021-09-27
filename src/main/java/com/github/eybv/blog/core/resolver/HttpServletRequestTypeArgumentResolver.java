package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Parameter;

@Component
public class HttpServletRequestTypeArgumentResolver implements ArgumentResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.getType().isAssignableFrom(HttpServletRequest.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (!canResolve(parameter)) {
            var info = new Object[] { parameter.getName(), parameter.getType().getName() };
            throw new UnsupportedParameterException(String.format("%s [%s]", info));
        }

        return request;
    }

}
