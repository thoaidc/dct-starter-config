package com.dct.config.autoconfig;

import com.dct.config.security.config.BaseSecurityFilterChainConfig;
import com.dct.config.security.config.DefaultBaseSecurityFilterChainConfig;
import com.dct.config.security.filter.BaseAuthenticationFilter;
import com.dct.config.security.filter.BaseHeaderSecurityFilter;
import com.dct.config.security.filter.BaseJwtFilter;
import com.dct.config.security.filter.DefaultJwtProvider;
import com.dct.config.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.config.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.model.config.properties.SecurityProps;
import com.dct.model.config.properties.SecurityProps.OAuth2Config;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.AuthenticationType;
import com.dct.model.constants.BasePropertiesConstants;
import com.dct.model.security.BaseJwtProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import java.util.Optional;

@AutoConfiguration
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
@ConditionalOnBean({
    AccessDeniedHandler.class,
    AuthenticationEntryPoint.class,
    SecurityProps.class
})
@EnableWebSecurity
public class SecurityFilterChainAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SecurityFilterChainAutoConfiguration.class);
    private final SecurityProps securityProps;

    public SecurityFilterChainAutoConfiguration(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Bean
    @ConditionalOnMissingBean(BaseJwtProvider.class)
    public BaseJwtProvider defaultJwtProvider(SecurityProps securityConfig) {
        log.debug("[JWT_PROVIDER_AUTO_CONFIG] - Use default JWT provider");
        return new DefaultJwtProvider(securityConfig);
    }

    @Bean
    @ConditionalOnProperty(
        name = BasePropertiesConstants.AUTHENTICATION_TYPE,
        havingValue = AuthenticationType.JWT_VERIFY_VALUE
    )
    @ConditionalOnMissingBean(BaseAuthenticationFilter.class)
    public BaseAuthenticationFilter defaultJwtFilter(SecurityProps securityProps, BaseJwtProvider jwtProvider) {
        log.debug("[AUTHENTICATION_FILTER_AUTO_CONFIG] - Use `BaseJwtFilter` as default authenticate filter");
        return new BaseJwtFilter(securityProps, jwtProvider);
    }

    @Bean
    @ConditionalOnProperty(
        name = BasePropertiesConstants.AUTHENTICATION_TYPE,
        havingValue = AuthenticationType.HEADER_FORWARDED_VALUE
    )
    @ConditionalOnMissingBean(BaseAuthenticationFilter.class)
    public BaseAuthenticationFilter defaultHeaderSecurityFilter(SecurityProps securityProps) {
        log.debug("[AUTHENTICATION_FILTER_AUTO_CONFIG] - Use `BaseHeaderSecurityFilter` as default authenticate filter");
        return new BaseHeaderSecurityFilter(securityProps);
    }

    @Bean
    @ConditionalOnMissingBean(BaseSecurityFilterChainConfig.class)
    public BaseSecurityFilterChainConfig baseSecurityFilterChainConfig(
        SecurityProps securityProps,
        AccessDeniedHandler accessDeniedHandler,
        AuthenticationEntryPoint authenticationEntryPoint,
        BaseAuthenticationFilter baseAuthenticationFilter,
        @Autowired(required = false) CorsFilter corsFilter,
        @Autowired(required = false) OAuth2AuthorizationRequestResolver oAuth2RequestResolver,
        @Autowired(required = false) BaseOAuth2AuthenticationSuccessHandler oAuth2SuccessHandler,
        @Autowired(required = false) BaseOAuth2AuthenticationFailureHandler oAuth2FailureHandler
    ) {
        log.debug("[SECURITY_FILTER_CHAIN_AUTO_CONFIG] - Use default security filter chain");
        return new DefaultBaseSecurityFilterChainConfig(
            securityProps,
            corsFilter,
            accessDeniedHandler,
            authenticationEntryPoint,
            baseAuthenticationFilter,
            oAuth2RequestResolver,
            oAuth2SuccessHandler,
            oAuth2FailureHandler
        );
    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(BaseSecurityFilterChainConfig securityFilterChainConfig,
                                                   MvcRequestMatcher.Builder mvc,
                                                   HttpSecurity http) throws Exception {
        log.debug("[SECURITY_FILTER_CHAIN_AUTO_CONFIG] - Using bean: `securityFilterChain`");
        securityFilterChainConfig.cors(http);
        securityFilterChainConfig.addFilters(http);
        securityFilterChainConfig.exceptionHandlers(http);
        securityFilterChainConfig.headersSecurity(http);
        securityFilterChainConfig.sessionManagementStrategy(http);
        securityFilterChainConfig.authorizeHttpRequests(http, mvc);

        OAuth2Config oAuth2Config = Optional.ofNullable(securityProps.getOauth2()).orElseGet(OAuth2Config::new);
        boolean enabledOAuth2 = ActivateStatus.ENABLED.equals(oAuth2Config.getActivate());

        if (enabledOAuth2) {
            securityFilterChainConfig.oauth2(http);
        }

        return http.build();
    }
}
