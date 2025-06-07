package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.config.properties.SecurityProps;
import com.dct.base.constants.PropertiesConstants;
import com.dct.base.security.jwt.BaseJwtFilter;
import com.dct.base.security.jwt.BaseJwtProvider;
import com.dct.base.security.jwt.DefaultJwtFilter;
import com.dct.base.security.jwt.DefaultJwtProvider;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_CONFIG, name = PropertiesConstants.SECRET_KEY_PROPERTY)
@EnableConfigurationProperties(SecurityProps.class)
public class JwtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BaseJwtProvider.class)
    protected BaseJwtProvider defaultJwtProvider(SecurityProps securityProps) {
        System.out.println("Default jwt provider");
        return new DefaultJwtProvider(securityProps);
    }

    @Bean
    @ConditionalOnMissingBean(BaseJwtFilter.class)
    protected BaseJwtFilter defaultJwtFilter(BaseJwtProvider jwtProvider, MessageTranslationUtils messageUtils) {
        System.out.println("Default jwt filter");
        return new DefaultJwtFilter(jwtProvider, messageUtils);
    }
}
