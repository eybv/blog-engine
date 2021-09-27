package com.github.eybv.blog.core.annotation;

import com.github.eybv.blog.core.request.MediaType;
import com.github.eybv.blog.core.request.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {
    RequestMethod method();
    String path();
    MediaType produce() default MediaType.APPLICATION_JSON;
}
