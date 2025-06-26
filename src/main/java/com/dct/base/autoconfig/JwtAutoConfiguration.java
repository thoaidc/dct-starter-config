package com.dct.base.autoconfig;

import com.dct.base.aop.BaseCheckAuthorizeAspect;
import com.dct.base.aop.DefaultCheckAuthorizeAspect;
import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.config.properties.JwtProps;
import com.dct.base.config.properties.SecurityProps;
import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.security.config.BaseSecurityAuthorizeRequestConfig;
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
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_JWT, havingValue = "true")
@EnableConfigurationProperties(JwtProps.class)
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
    protected BaseJwtFilter defaultJwtFilter(BaseSecurityAuthorizeRequestConfig authorizeRequestConfig,
                                             BaseJwtProvider jwtProvider,
                                             MessageTranslationUtils messageUtils) {
        log.debug("[{}] - Auto configure default JWT Filter", ENTITY_NAME);
        return new DefaultJwtFilter(authorizeRequestConfig, jwtProvider, messageUtils);
    }

    @Bean
    @ConditionalOnMissingBean(BaseCheckAuthorizeAspect.class)
    public BaseCheckAuthorizeAspect defaultCheckAuthorizeAspect() {
        log.debug("[{}] - Auto configure default check authorize aspect", ENTITY_NAME);
        return new DefaultCheckAuthorizeAspect();
    }
}
