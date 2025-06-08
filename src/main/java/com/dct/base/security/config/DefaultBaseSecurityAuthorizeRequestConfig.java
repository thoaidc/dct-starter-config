package com.dct.base.security.config;

import com.dct.base.constants.BaseSecurityConstants;

public class DefaultBaseSecurityAuthorizeRequestConfig extends BaseSecurityAuthorizeRequestConfig {

    @Override
    public String[] getPublicPatterns() {
        return BaseSecurityConstants.REQUEST_MATCHERS.DEFAULT_PUBLIC_API_PATTERNS;
    }
}
