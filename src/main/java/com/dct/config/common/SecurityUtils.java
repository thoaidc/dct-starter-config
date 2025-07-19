package com.dct.config.common;

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SecurityUtils {

    public static MvcRequestMatcher[] convertToMvcMatchers(MvcRequestMatcher.Builder mvc, String[] patterns) {
        return Stream.of(patterns)
            .map(mvc::pattern)
            .toList()
            .toArray(new MvcRequestMatcher[0]);
    }
}
