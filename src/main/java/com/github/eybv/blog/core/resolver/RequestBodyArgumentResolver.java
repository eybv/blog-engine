package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.RequestBody;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Parameter;

@Component
public class RequestBodyArgumentResolver implements ArgumentResolver {

    private final Gson gson = new Gson();

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (!canResolve(parameter)) {
            var info = new Object[] { parameter.getName(), parameter.getType().getName() };
            throw new UnsupportedParameterException(String.format("%s [%s]", info));
        }

        try {
            return gson.fromJson(request.getReader(), parameter.getType());
        } catch (Exception e) {
            throw new ArgumentResolveException(e);
        }
    }

}
