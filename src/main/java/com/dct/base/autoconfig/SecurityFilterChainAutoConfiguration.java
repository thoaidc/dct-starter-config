package com.dct.base.autoconfig;

import com.dct.base.security.config.BaseSecurityAuthorizeRequestConfig;
import com.dct.base.security.config.BaseSecurityFilterChainConfig;
import com.dct.base.security.config.DefaultBaseSecurityFilterChainConfig;
import com.dct.base.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.base.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.base.security.jwt.BaseJwtFilter;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;

@AutoConfiguration
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
@ConditionalOnBean({
    CorsFilter.class,
    AccessDeniedHandler.class,
    AuthenticationEntryPoint.class,
    BaseSecurityAuthorizeRequestConfig.class
})
@EnableWebSecurity
public class SecurityFilterChainAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilterChainAutoConfiguration.class);
    private static final String ENTITY_NAME = "SecurityFilterChainAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseSecurityFilterChainConfig.class)
    public BaseSecurityFilterChainConfig defaultBaseSecurityFilterChainConfig(
        CorsFilter corsFilter,
        AccessDeniedHandler accessDeniedHandler,
        AuthenticationEntryPoint authenticationEntryPoint,
        BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
        @Autowired(required = false) BaseJwtFilter jwtFilter,
        @Autowired(required = false) OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver,
        @Autowired(required = false) BaseOAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
        @Autowired(required = false) BaseOAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
        Environment env
    ) {
        boolean jwtEnabled = Boolean.parseBoolean(env.getProperty(BasePropertiesConstants.ENABLED_JWT));
        boolean oauth2Enabled = Boolean.parseBoolean(env.getProperty(BasePropertiesConstants.ENABLED_OAUTH2));

        if (jwtEnabled && oauth2Enabled) {
            log.debug("[{}] - Auto configure security filter chain with JWT + OAuth2", ENTITY_NAME);
            return new DefaultBaseSecurityFilterChainConfig(
                corsFilter,
                securityAuthorizeRequestConfig,
                accessDeniedHandler,
                authenticationEntryPoint,
                jwtFilter,
                oAuth2AuthorizationRequestResolver,
                oAuth2AuthenticationSuccessHandler,
                oAuth2AuthenticationFailureHandler
            );
        } else if (jwtEnabled) {
            log.debug("[{}] - Auto configure security filter chain with JWT only", ENTITY_NAME);
            return new DefaultBaseSecurityFilterChainConfig(
                corsFilter,
                securityAuthorizeRequestConfig,
                accessDeniedHandler,
                authenticationEntryPoint,
                jwtFilter
            );
        }

        log.debug("[{}] - Auto configure default security filter chain", ENTITY_NAME);
        return new DefaultBaseSecurityFilterChainConfig(
            corsFilter,
            securityAuthorizeRequestConfig,
            accessDeniedHandler,
            authenticationEntryPoint
        );
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    protected SecurityFilterChain defaulSecurityFilterChain(BaseSecurityFilterChainConfig securityFilterChainConfig,
                                                            MvcRequestMatcher.Builder mvc,
                                                            HttpSecurity http) throws Exception {
        log.debug("[{}] - Using bean: `defaultSecurityFilterChain`", ENTITY_NAME);
        securityFilterChainConfig.cors(http);
        securityFilterChainConfig.addFilters(http);
        securityFilterChainConfig.exceptionHandlers(http);
        securityFilterChainConfig.headersSecurity(http);
        securityFilterChainConfig.sessionManagementStrategy(http);
        securityFilterChainConfig.authorizeHttpRequests(http, mvc);
        securityFilterChainConfig.oauth2(http);
        return http.build();
    }
}
