package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.exception.handler.BaseExceptionHandler;
import com.dct.base.exception.handler.DefaultBaseExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static com.dct.base.constants.ActivateStatus.ENABLED_VALUE;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_EXCEPTION_HANDLER, havingValue = ENABLED_VALUE)
public class GlobalExceptionHandlerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandlerAutoConfiguration.class);
    private static final String ENTITY_NAME = "GlobalExceptionHandlerAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseExceptionHandler.class)
    public BaseExceptionHandler defaultBaseExceptionHandler(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[{}] - Auto configure default global exception handler", ENTITY_NAME);
        return new DefaultBaseExceptionHandler(messageTranslationUtils);
    }
}
