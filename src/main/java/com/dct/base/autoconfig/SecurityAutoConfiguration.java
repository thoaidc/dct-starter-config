package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.security.handler.DefaultBaseAccessDeniedHandler;
import com.dct.base.security.handler.DefaultBaseAuthenticationEntryPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@AutoConfiguration
public class SecurityAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
    private static final String ENTITY_NAME = "SecurityAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler defaultAccessDeniedHandler(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[{}] - Auto configure default access denied handler", ENTITY_NAME);
        return new DefaultBaseAccessDeniedHandler(messageTranslationUtils);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint defaultAuthenticationEntryPoint(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[{}] - Auto configure default authentication entry point", ENTITY_NAME);
        return new DefaultBaseAuthenticationEntryPoint(messageTranslationUtils);
    }
}
