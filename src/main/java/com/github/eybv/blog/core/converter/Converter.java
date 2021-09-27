package com.github.eybv.blog.core.converter;

@FunctionalInterface
public interface Converter<T, R> {

    R convert(T from);

}
