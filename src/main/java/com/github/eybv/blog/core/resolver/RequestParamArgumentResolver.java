package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class RequestParamArgumentResolver implements ArgumentResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class) && (isString(parameter)|| isCollection(parameter));
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (!canResolve(parameter)) {
            var info = new Object[] { parameter.getName(), parameter.getType().getName() };
            throw new UnsupportedParameterException(String.format("%s [%s]", info));
        }

        final var annotation = parameter.getAnnotation(RequestParam.class);

        if (!request.getParameterMap().containsKey(annotation.value()) && annotation.required()) {
            var error = String.format("Parameter %s not present", annotation.value());
            throw new ArgumentResolveException(error);
        }

        if (!request.getParameterMap().containsKey(annotation.value()) && !annotation.required()) {
            return null;
        }

        final var param = request.getParameterMap().get(annotation.value());

        if (isString(parameter) && param.length > 1) {
            throw new ArgumentResolveException("Collection cannot be cast to String");
        }

        return isString(parameter) ? param[0] : Arrays.stream(param).collect(Collectors.toList());
    }

    private boolean isString(Parameter parameter) {
        return parameter.getType().isAssignableFrom(String.class);
    }

    private boolean isCollection(Parameter parameter) {
        return Collection.class.isAssignableFrom(parameter.getType());
    }

}
