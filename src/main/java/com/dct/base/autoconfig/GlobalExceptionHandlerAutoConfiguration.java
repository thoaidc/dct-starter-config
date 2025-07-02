package com.dct.base.autoconfig;

import com.dct.base.exception.BaseExceptionHandler;
import com.dct.base.exception.DefaultBaseExceptionHandler;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Objects;

import static com.dct.model.constants.ActivateStatus.ENABLED_VALUE;

@AutoConfiguration
public class GlobalExceptionHandlerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandlerAutoConfiguration.class);
    private static final String ENTITY_NAME = "GlobalExceptionHandlerAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseExceptionHandler.class)
    public BaseExceptionHandler defaultBaseExceptionHandler(
        @Autowired(required = false) MessageTranslationUtils messageTranslationUtils,
        Environment env
    ) {
        log.debug("[{}] - Auto configure default global exception handler", ENTITY_NAME);
        String isI18nEnabled = env.getProperty(BasePropertiesConstants.ENABLED_I18N);

        if (ENABLED_VALUE.equals(isI18nEnabled) && Objects.nonNull(messageTranslationUtils)) {
            return new DefaultBaseExceptionHandler(messageTranslationUtils);
        }

        return new DefaultBaseExceptionHandler();
    }
}
