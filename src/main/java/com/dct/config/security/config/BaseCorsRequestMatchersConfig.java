package com.dct.config.security.config;

import java.util.List;

@SuppressWarnings("unused")
public interface BaseCorsRequestMatchersConfig {

    List<String> applyFor();
    List<String> getAllowedOriginPatterns();
    List<String> getAllowedHeaders();
    List<String> getAllowedMethods();
    boolean isAllowCredentials();
}
