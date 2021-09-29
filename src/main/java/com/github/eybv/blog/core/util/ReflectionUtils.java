package com.github.eybv.blog.core.util;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

public class ReflectionUtils {

    private ReflectionUtils() { }

    public static Class<?> getParameterType(Parameter parameter) throws ClassNotFoundException {
        final var parametrizedType = (ParameterizedType) parameter.getParameterizedType();
        final var parameterTypeName = parametrizedType.getActualTypeArguments()[0].getTypeName();

        return Class.forName(parameterTypeName);
    }

}
