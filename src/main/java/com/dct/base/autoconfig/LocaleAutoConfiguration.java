package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LocaleAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LocaleAutoConfiguration.class);
    private static final String ENTITY_NAME = "LocaleAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(MessageTranslationUtils.class)
    public MessageTranslationUtils messageTranslationUtils(MessageSource messageSource) {
        log.debug("[{}] - Auto configure default message translation utils", ENTITY_NAME);
        return new MessageTranslationUtils(messageSource);
    }
}
