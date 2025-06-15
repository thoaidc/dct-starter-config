package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.constants.BaseCommonConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Supports internationalization (i18n) and integration with validation <p>
 * Useful when using Hibernate Validator with annotations like @NotNull, @Size,... <p>
 * In Spring, {@link LocaleResolver} determines the current language of the application based on the HTTP request. <p>
 * {@link AcceptHeaderLocaleResolver} automatically analyzes the value of the Accept-Language header in each request
 * and selects the locale <p>
 * This {@link Locale} value is used to retrieve internationalized messages (I18n)
 * @author thoaidc
 */
@AutoConfiguration
public class LocaleAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LocaleAutoConfiguration.class);
    private static final String ENTITY_NAME = "LocaleAutoConfiguration";

    @Bean
    public LocaleResolver defaultLocaleResolver() {
        log.debug("[{}] - Auto configure AcceptHeaderLocaleResolver as default local resolver", ENTITY_NAME);
        return new AcceptHeaderLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public MessageSource messageSource() {
        log.debug("[{}] - Auto configure default MessageSource", ENTITY_NAME);
        // Provides a mechanism to load notifications from .properties files to support i18n
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Set the location of the message files
        // Spring will look for files by name messages_{locale}.properties
        messageSource.setBasenames(BaseCommonConstants.MESSAGE_SOURCE_BASENAME);
        messageSource.setDefaultEncoding(BaseCommonConstants.MESSAGE_SOURCE_ENCODING);
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(MessageTranslationUtils.class)
    public MessageTranslationUtils messageTranslationUtils(MessageSource messageSource) {
        log.debug("[{}] - Auto configure default message translation utils", ENTITY_NAME);
        return new MessageTranslationUtils(messageSource);
    }

    @Bean
    public LocalValidatorFactoryBean defaultLocalValidatorFactoryBean(MessageSource messageSource) {
        log.debug("[{}] - Auto configure default MessageSource for hibernate validation", ENTITY_NAME);
        // Connect the validation to MessageSource to get error messages from message bundles
        // When a validation error occurs, Spring looks for the error message from the .properties files provided
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
