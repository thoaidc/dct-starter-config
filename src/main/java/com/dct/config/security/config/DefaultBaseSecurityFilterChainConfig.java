package com.dct.config.security.config;

import com.dct.config.security.filter.BaseAuthenticationFilter;

import com.dct.model.config.properties.SecurityProps;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.CorsFilter;

public class DefaultBaseSecurityFilterChainConfig extends BaseSecurityFilterChainConfig {

    public DefaultBaseSecurityFilterChainConfig(SecurityProps securityProps,
                                                CorsFilter corsFilter,
                                                AccessDeniedHandler accessDeniedHandler,
                                                AuthenticationEntryPoint authenticationEntryPoint,
                                                BaseAuthenticationFilter baseAuthenticationFilter) {
        super(securityProps, corsFilter, accessDeniedHandler, authenticationEntryPoint, baseAuthenticationFilter);
    }
}
