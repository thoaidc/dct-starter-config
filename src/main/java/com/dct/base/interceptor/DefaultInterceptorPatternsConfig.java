package com.dct.base.interceptor;

import com.dct.base.constants.BaseCommonConstants;

public class DefaultInterceptorPatternsConfig implements InterceptorPatternsConfig {

    @Override
    public String[] excludedPaths() {
        return BaseCommonConstants.INTERCEPTOR_EXCLUDED_PATHS;
    }
}
