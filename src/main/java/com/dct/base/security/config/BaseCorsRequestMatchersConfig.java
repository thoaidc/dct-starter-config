package com.dct.base.security.config;

import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseCorsRequestMatchersConfig {

    public abstract List<String> applyFor();
    public abstract List<String> getAllowedOriginPatterns();
    public abstract List<String> getAllowedHeaders();
    public abstract List<String> getAllowedMethods();
    public abstract boolean isAllowCredentials();
}
