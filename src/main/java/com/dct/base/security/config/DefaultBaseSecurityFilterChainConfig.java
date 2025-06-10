package com.dct.base.security.config;

import com.dct.base.security.jwt.BaseJwtFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.CorsFilter;

public class DefaultBaseSecurityFilterChainConfig extends BaseSecurityFilterChainConfig {

    public DefaultBaseSecurityFilterChainConfig(CorsFilter corsFilter,
                                                BaseJwtFilter jwtFilter,
                                                AccessDeniedHandler accessDeniedHandler,
                                                AuthenticationEntryPoint authenticationEntryPoint,
                                                BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig) {
        super(corsFilter, jwtFilter, accessDeniedHandler, authenticationEntryPoint, securityAuthorizeRequestConfig);
    }
}
