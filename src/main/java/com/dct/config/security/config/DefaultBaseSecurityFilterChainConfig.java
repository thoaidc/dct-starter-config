package com.dct.config.security.config;

import com.dct.config.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.config.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.config.security.jwt.BaseJwtFilter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.CorsFilter;

import java.util.Objects;

public class DefaultBaseSecurityFilterChainConfig extends BaseSecurityFilterChainConfig {

    private OAuth2AuthorizationRequestResolver oAuth2RequestResolver;
    private BaseOAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private BaseOAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    public DefaultBaseSecurityFilterChainConfig(CorsFilter corsFilter,
                                                BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                                AccessDeniedHandler accessDeniedHandler,
                                                AuthenticationEntryPoint authenticationEntryPoint) {
        super(corsFilter, securityAuthorizeRequestConfig, accessDeniedHandler, authenticationEntryPoint);
    }

    public DefaultBaseSecurityFilterChainConfig(CorsFilter corsFilter,
                                                BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                                AccessDeniedHandler accessDeniedHandler,
                                                AuthenticationEntryPoint authenticationEntryPoint,
                                                BaseJwtFilter jwtFilter) {
        super(corsFilter, securityAuthorizeRequestConfig, accessDeniedHandler, authenticationEntryPoint, jwtFilter);
    }

    public DefaultBaseSecurityFilterChainConfig(CorsFilter corsFilter,
                                                BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                                AccessDeniedHandler accessDeniedHandler,
                                                AuthenticationEntryPoint authenticationEntryPoint,
                                                BaseJwtFilter jwtFilter,
                                                OAuth2AuthorizationRequestResolver oAuth2RequestResolver,
                                                BaseOAuth2AuthenticationSuccessHandler oAuth2SuccessHandler,
                                                BaseOAuth2AuthenticationFailureHandler oAuth2FailureHandler) {
        super(corsFilter, securityAuthorizeRequestConfig, accessDeniedHandler, authenticationEntryPoint, jwtFilter);
        this.oAuth2RequestResolver = oAuth2RequestResolver;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
    }

    @Override
    public void oauth2(HttpSecurity http) throws Exception {
        if (Objects.nonNull(oAuth2RequestResolver)
            && Objects.nonNull(oAuth2FailureHandler)
            && Objects.nonNull(oAuth2SuccessHandler)
        ) {
            http.oauth2Login(oAuth2Config -> oAuth2Config
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
                .authorizationEndpoint(config ->
                    config.authorizationRequestResolver(oAuth2RequestResolver))
            );
        }
    }
}
