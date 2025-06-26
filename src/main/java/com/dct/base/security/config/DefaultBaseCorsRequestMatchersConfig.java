package com.dct.base.security.config;

import com.dct.base.config.properties.InterceptorProps;
import com.dct.base.config.properties.InterceptorProps.CorsConfig;
import com.dct.base.constants.BaseSecurityConstants;

import java.util.List;
import java.util.Optional;

public class DefaultBaseCorsRequestMatchersConfig implements BaseCorsRequestMatchersConfig {

    private final InterceptorProps.CorsConfig corsConfig;

    public DefaultBaseCorsRequestMatchersConfig(InterceptorProps interceptorProps) {
        InterceptorProps interceptorConfig = Optional.ofNullable(interceptorProps).orElse(new InterceptorProps());
        this.corsConfig = Optional.ofNullable(interceptorConfig.getCors()).orElse(new CorsConfig());
    }

    @Override
    public List<String> applyFor() {
        return Optional.ofNullable(corsConfig.getApplyFor())
                .orElse(List.of(BaseSecurityConstants.CORS.DEFAULT_APPLY_FOR));
    }

    @Override
    public List<String> getAllowedOriginPatterns() {
        return Optional.ofNullable(corsConfig.getAllowedOriginPatterns())
                .orElse(List.of(BaseSecurityConstants.CORS.DEFAULT_ALLOWED_ORIGIN_PATTERNS));
    }

    @Override
    public List<String> getAllowedHeaders() {
        return Optional.ofNullable(corsConfig.getAllowedHeaders())
                .orElse(List.of(BaseSecurityConstants.CORS.DEFAULT_ALLOWED_HEADERS));
    }

    @Override
    public List<String> getAllowedMethods() {
        return Optional.ofNullable(corsConfig.getAllowedMethods())
                .orElse(List.of(BaseSecurityConstants.CORS.DEFAULT_ALLOWED_REQUEST_METHODS));
    }

    @Override
    public boolean isAllowCredentials() {
        return Optional.ofNullable(corsConfig.isAllowedCredentials())
                .orElse(BaseSecurityConstants.CORS.DEFAULT_ALLOW_CREDENTIALS);
    }
}
