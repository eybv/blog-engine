package com.github.eybv.blog.core.converter;

import com.google.gson.Gson;

public class ObjectToJsonConverter implements Converter<Object, String> {

    private final Gson gson = new Gson();

    @Override
    public String convert(Object from) {
        return gson.toJson(from);
    }

}
