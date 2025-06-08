package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.config.properties.SecurityProps;
import com.dct.base.constants.PropertiesConstants;
import com.dct.base.security.jwt.BaseJwtFilter;
import com.dct.base.security.jwt.BaseJwtProvider;
import com.dct.base.security.jwt.DefaultJwtFilter;
import com.dct.base.security.jwt.DefaultJwtProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_CONFIG, name = PropertiesConstants.SECRET_KEY_PROPERTY)
@EnableConfigurationProperties(SecurityProps.class)
public class JwtAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtAutoConfiguration.class);
    private static final String ENTITY_NAME = "JwtAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseJwtProvider.class)
    protected BaseJwtProvider defaultJwtProvider(SecurityProps securityProps) {
        log.debug("[{}] - Auto configure default JWT provider", ENTITY_NAME);
        return new DefaultJwtProvider(securityProps);
    }

    @Bean
    @ConditionalOnMissingBean(BaseJwtFilter.class)
    protected BaseJwtFilter defaultJwtFilter(BaseJwtProvider jwtProvider, MessageTranslationUtils messageUtils) {
        log.debug("[{}] - Auto configure default JWT Filter", ENTITY_NAME);
        return new DefaultJwtFilter(jwtProvider, messageUtils);
    }
}
