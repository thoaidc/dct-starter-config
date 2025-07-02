package com.dct.base.security.config;

import com.dct.model.config.properties.SecurityProps;

import java.util.Optional;

public class DefaultBaseSecurityAuthorizeRequestConfig implements BaseSecurityAuthorizeRequestConfig {

    private final SecurityProps securityProps;

    public DefaultBaseSecurityAuthorizeRequestConfig(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Override
    public String[] getPublicPatterns() {
        return Optional.ofNullable(securityProps).orElse(new SecurityProps()).getPublicRequestPatterns();
    }
}
