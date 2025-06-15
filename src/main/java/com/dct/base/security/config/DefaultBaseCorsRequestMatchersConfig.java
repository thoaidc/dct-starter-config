package com.dct.base.security.config;

import com.dct.base.constants.BaseSecurityConstants;

import java.util.List;

public class DefaultBaseCorsRequestMatchersConfig implements BaseCorsRequestMatchersConfig {

    @Override
    public List<String> applyFor() {
        return List.of(BaseSecurityConstants.CORS.DEFAULT_APPLY_FOR);
    }

    @Override
    public List<String> getAllowedOriginPatterns() {
        return List.of(BaseSecurityConstants.CORS.DEFAULT_ALLOWED_ORIGIN_PATTERNS);
    }

    @Override
    public List<String> getAllowedHeaders() {
        return List.of(BaseSecurityConstants.CORS.DEFAULT_ALLOWED_HEADERS);
    }

    @Override
    public List<String> getAllowedMethods() {
        return List.of(BaseSecurityConstants.CORS.DEFAULT_ALLOWED_REQUEST_METHODS);
    }

    @Override
    public boolean isAllowCredentials() {
        return BaseSecurityConstants.CORS.DEFAULT_ALLOW_CREDENTIALS;
    }
}
