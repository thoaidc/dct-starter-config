package com.dct.base.autoconfig;

import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.security.config.BaseSecurityAuthorizeRequestConfig;
import com.dct.base.security.config.BaseSecurityFilterChainConfig;
import com.dct.base.security.config.DefaultBaseSecurityFilterChainConfig;
import com.dct.base.security.config.DefaultBaseSecurityFilterChainWithOAuth2Config;
import com.dct.base.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.base.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.base.security.jwt.BaseJwtFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
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
    BaseJwtFilter.class,
    AuthenticationEntryPoint.class,
    AccessDeniedHandler.class,
    BaseSecurityAuthorizeRequestConfig.class
})
@EnableWebSecurity
public class SecurityFilterChainAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilterChainAutoConfiguration.class);
    private static final String ENTITY_NAME = "SecurityFilterChainAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseSecurityFilterChainConfig.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_OAUTH2, havingValue = "false", matchIfMissing = true)
    public BaseSecurityFilterChainConfig defaultBaseSecurityFilterChainJWTOnlyConfig(
        CorsFilter corsFilter,
        BaseJwtFilter jwtFilter,
        AccessDeniedHandler accessDeniedHandler,
        AuthenticationEntryPoint authenticationEntryPoint,
        BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig
    ) {
        log.debug("[{}] - Auto configure default security filter chain", ENTITY_NAME);
        return new DefaultBaseSecurityFilterChainConfig(
            corsFilter,
            jwtFilter,
            accessDeniedHandler,
            authenticationEntryPoint,
            securityAuthorizeRequestConfig
        );
    }

    @Bean
    @ConditionalOnMissingBean(BaseSecurityFilterChainConfig.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_OAUTH2, havingValue = "true")
    public BaseSecurityFilterChainConfig defaultBaseSecurityFilterChainWithOAuth2Config(
        CorsFilter corsFilter,
        BaseJwtFilter jwtFilter,
        AccessDeniedHandler accessDeniedHandler,
        AuthenticationEntryPoint authenticationEntryPoint,
        BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
        OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver,
        BaseOAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
        BaseOAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler
    ) {
        log.debug("[{}] - Auto configure default security filter chain with OAuth2 config", ENTITY_NAME);
        return new DefaultBaseSecurityFilterChainWithOAuth2Config(
            corsFilter,
            jwtFilter,
            accessDeniedHandler,
            authenticationEntryPoint,
            securityAuthorizeRequestConfig,
            oAuth2AuthorizationRequestResolver,
            oAuth2AuthenticationSuccessHandler,
            oAuth2AuthenticationFailureHandler
        );
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    protected SecurityFilterChain defaulSecurityFilterChain(BaseSecurityFilterChainConfig securityFilterChainConfig,
                                                      HttpSecurity http,
                                                      MvcRequestMatcher.Builder mvc) throws Exception {
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
