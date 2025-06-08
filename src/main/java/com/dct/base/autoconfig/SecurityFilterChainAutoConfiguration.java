package com.dct.base.autoconfig;

import com.dct.base.security.config.BaseSecurityAuthorizeRequestConfig;
import com.dct.base.security.config.BaseSecurityFilterChain;
import com.dct.base.security.jwt.BaseJwtFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
public class SecurityFilterChainAutoConfiguration extends BaseSecurityFilterChain {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilterChainAutoConfiguration.class);
    private static final String ENTITY_NAME = "SecurityFilterChainAutoConfiguration";

    protected SecurityFilterChainAutoConfiguration(CorsFilter corsFilter,
                                                   BaseJwtFilter jwtFilter,
                                                   AccessDeniedHandler accessDeniedHandler,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig) {
        super(corsFilter, jwtFilter, accessDeniedHandler, authenticationEntryPoint, securityAuthorizeRequestConfig);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        log.debug("[{}] - Auto configure default security filter chain", ENTITY_NAME);
        return super.securityFilterChain(http, mvc);
    }
}
