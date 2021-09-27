package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.RequestHeader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Parameter;

@Component
public class RequestHeaderArgumentResolver implements ArgumentResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.getType().isAssignableFrom(String.class) &&
                parameter.isAnnotationPresent(RequestHeader.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (!canResolve(parameter)) {
            var info = new Object[] { parameter.getName(), parameter.getType().getName() };
            throw new UnsupportedParameterException(String.format("%s [%s]", info));
        }

        final var annotation = parameter.getAnnotation(RequestHeader.class);
        final var error = String.format("Header %s not present", annotation.value());

        final var header = request.getHeader(annotation.value());

        if (header == null && annotation.required()) {
            throw new ArgumentResolveException(error);
        }

        return header;
    }

}
