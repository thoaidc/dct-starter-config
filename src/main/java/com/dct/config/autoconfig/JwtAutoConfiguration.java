package com.dct.config.autoconfig;

import com.dct.config.aop.BaseCheckAuthorizeAspect;
import com.dct.config.aop.DefaultCheckAuthorizeAspect;
import com.dct.config.security.config.BaseSecurityAuthorizeRequestConfig;
import com.dct.config.security.jwt.BaseJwtFilter;
import com.dct.config.security.jwt.BaseJwtProvider;
import com.dct.config.security.jwt.DefaultJwtFilter;
import com.dct.config.security.jwt.DefaultJwtProvider;
import com.dct.model.constants.BasePropertiesConstants;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.config.properties.JwtProps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Objects;

import static com.dct.model.constants.ActivateStatus.ENABLED_VALUE;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_JWT, havingValue = "true")
@EnableConfigurationProperties(JwtProps.class)
public class JwtAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtAutoConfiguration.class);
    private static final String ENTITY_NAME = "JwtAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseJwtProvider.class)
    protected BaseJwtProvider defaultJwtProvider(JwtProps jwtProps) {
        log.debug("[{}] - Auto configure default JWT provider", ENTITY_NAME);
        return new DefaultJwtProvider(jwtProps);
    }

    @Bean
    @ConditionalOnMissingBean(BaseJwtFilter.class)
    protected BaseJwtFilter defaultJwtFilter(BaseSecurityAuthorizeRequestConfig authorizeRequestConfig,
                                             BaseJwtProvider jwtProvider,
                                             @Autowired(required = false) MessageTranslationUtils messageUtils,
                                             Environment env) {
        log.debug("[{}] - Auto configure default JWT Filter", ENTITY_NAME);
        String isI18nEnabled = env.getProperty(BasePropertiesConstants.ENABLED_I18N);

        if (ENABLED_VALUE.equals(isI18nEnabled) && Objects.nonNull(messageUtils)) {
            return new DefaultJwtFilter(authorizeRequestConfig, jwtProvider, messageUtils);
        }

        return new DefaultJwtFilter(authorizeRequestConfig, jwtProvider);
    }

    @Bean
    @ConditionalOnMissingBean(BaseCheckAuthorizeAspect.class)
    public BaseCheckAuthorizeAspect defaultCheckAuthorizeAspect() {
        log.debug("[{}] - Auto configure default check authorize aspect", ENTITY_NAME);
        return new DefaultCheckAuthorizeAspect();
    }
}
