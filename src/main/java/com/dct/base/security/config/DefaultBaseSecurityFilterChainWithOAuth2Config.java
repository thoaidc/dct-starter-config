package com.dct.base.security.config;

import com.dct.base.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.base.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.base.security.jwt.BaseJwtFilter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.CorsFilter;

public class DefaultBaseSecurityFilterChainWithOAuth2Config extends BaseSecurityFilterChainConfig {

    private final OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;
    private final BaseOAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final BaseOAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public DefaultBaseSecurityFilterChainWithOAuth2Config(
        CorsFilter corsFilter,
        BaseJwtFilter jwtFilter,
        AccessDeniedHandler accessDeniedHandler,
        AuthenticationEntryPoint authenticationEntryPoint,
        BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
        OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver,
        BaseOAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
        BaseOAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler
    ) {
        super(corsFilter, jwtFilter, accessDeniedHandler, authenticationEntryPoint, securityAuthorizeRequestConfig);
        this.oAuth2AuthorizationRequestResolver = oAuth2AuthorizationRequestResolver;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Override
    public void oauth2(HttpSecurity http) throws Exception {
        http.oauth2Login(oAuth2Config -> oAuth2Config
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler)
            .authorizationEndpoint(config ->
                        config.authorizationRequestResolver(oAuth2AuthorizationRequestResolver))
        );
    }
}
