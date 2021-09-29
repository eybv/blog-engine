package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.RequestParam;
import com.github.eybv.blog.core.util.ReflectionUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class RequestParamArgumentResolver implements ArgumentResolver {

    @Override
    @SneakyThrows(ClassNotFoundException.class)
    public boolean canResolve(Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            if (Collection.class.isAssignableFrom(parameter.getType())) {
                return supportsType(ReflectionUtils.getParameterType(parameter));
            }
            return supportsType(parameter.getType());
        }
        return false;
    }

    @Override
    @SneakyThrows(ClassNotFoundException.class)
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

        final var type = isCollection(parameter) ? ReflectionUtils.getParameterType(parameter) : parameter.getType();

        final var param = Arrays.stream(request.getParameterMap().get(annotation.value()))
                .map(x -> convert(x, type))
                .collect(Collectors.toUnmodifiableList());

        if (!isCollection(parameter) && param.size() > 1) {
            throw new ArgumentResolveException("Collection cannot be cast to String");
        }

        return isCollection(parameter) ? param : param.get(0);
    }

    private Object convert(String value, Class<?> type) {
        if (Integer.class.isAssignableFrom(type)) {
            return Integer.valueOf(value);
        }
        if (Long.class.isAssignableFrom(type)) {
            return Long.valueOf(value);
        }
        if (Double.class.isAssignableFrom(type)) {
            return Double.valueOf(value);
        }
        return value;
    }

    private boolean supportsType(Class<?> type) {
        return String.class.isAssignableFrom(type) ||
                Integer.class.isAssignableFrom(type) ||
                Long.class.isAssignableFrom(type) ||
                Double.class.isAssignableFrom(type);
    }

    private boolean isCollection(Parameter parameter) {
        return Collection.class.isAssignableFrom(parameter.getType());
    }

}
