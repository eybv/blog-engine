package com.github.eybv.blog.core.converter;

import com.github.eybv.blog.core.request.MediaType;
import com.github.eybv.blog.core.annotation.Component;

@Component
public class TextMediaTypeConverterFactory {

    public Converter<Object, String> getConverter(MediaType mediaType) {
        return switch (mediaType) {
            case TEXT_PLAIN -> new ObjectToStringConverter();
            case APPLICATION_JSON -> new ObjectToJsonConverter();
        };
    }

}
