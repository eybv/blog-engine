package com.github.eybv.blog.core.request;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ToString(of = "template")
@EqualsAndHashCode(of = "template")
public class RequestMatcher {

    private final Set<String> pathVariableKeys;

    private final String template;

    private final Pattern pattern;

    public RequestMatcher(String template) {
        this.template = template;
        pathVariableKeys = Pattern.compile("\\{(\\w+)}")
                .matcher(template)
                .results()
                .map(x -> x.group(1))
                .collect(Collectors.toUnmodifiableSet());
        final var preparedPattern = template
                .replaceAll("\\{(\\w+)}", "(?<$1>(\\\\d|\\\\w)+)/{0,1}")
                .replaceAll("\\{0,1}/", "");
        pattern = Pattern.compile(preparedPattern);
    }

    public boolean matches(String path) {
        return pattern.matcher(path).matches();
    }

    public Map<String, String> getPathVariables(String path) {
        final var matcher = pattern.matcher(path);
        // Do not remove!!!
        // See Matcher.group(String name)
        matcher.matches();
        final var map = new HashMap<String, String>();
        pathVariableKeys.forEach(key -> map.put(key, matcher.group(key)));
        return Collections.unmodifiableMap(map);
    }

}
