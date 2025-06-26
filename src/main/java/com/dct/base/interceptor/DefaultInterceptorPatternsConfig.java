package com.dct.base.interceptor;

import com.dct.base.config.properties.InterceptorProps;
import com.dct.base.constants.BaseCommonConstants;

import java.util.Optional;

public class DefaultInterceptorPatternsConfig implements InterceptorPatternsConfig {

    private final InterceptorProps interceptorProps;

    public DefaultInterceptorPatternsConfig(InterceptorProps interceptorProps) {
        this.interceptorProps = Optional.ofNullable(interceptorProps).orElse(new InterceptorProps());
    }

    @Override
    public String[] excludedPaths() {
        return Optional.ofNullable(interceptorProps.getExcludedPatterns())
                .orElse(BaseCommonConstants.INTERCEPTOR_EXCLUDED_PATHS);
    }
}
