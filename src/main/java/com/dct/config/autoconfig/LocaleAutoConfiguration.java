package com.dct.config.autoconfig;

import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.config.properties.I18nProps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;
import java.util.Optional;

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
@EnableConfigurationProperties(I18nProps.class)
public class LocaleAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(LocaleAutoConfiguration.class);
    private final I18nProps i18nProps;

    public LocaleAutoConfiguration(I18nProps i18nProps) {
        this.i18nProps = i18nProps;
    }

    @Bean
    public LocaleResolver defaultLocaleResolver() {
        log.debug("[LOCALE_RESOLVER_AUTO_CONFIG] - Use `AcceptHeaderLocaleResolver` as default local resolver");
        return new AcceptHeaderLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public MessageSource messageSource() {
        log.debug("[MESSAGE_SOURCE_AUTO_CONFIG] - Use default MessageSource");
        // Provides a mechanism to load notifications from .properties files to support i18n
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Set the location of the message files
        // Spring will look for files by name messages_{locale}.properties
        messageSource.setBasenames(Optional.ofNullable(i18nProps).orElse(new I18nProps()).getBaseNames());
        messageSource.setDefaultEncoding(Optional.ofNullable(i18nProps).orElse(new I18nProps()).getEncoding());
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(MessageTranslationUtils.class)
    public MessageTranslationUtils messageTranslationUtils(MessageSource messageSource) {
        log.debug("[MESSAGE_TRANSLATION_AUTO_CONFIG] - Use default message translation utils");
        return new MessageTranslationUtils(messageSource);
    }

    @Bean
    public LocalValidatorFactoryBean defaultLocalValidatorFactoryBean(MessageSource messageSource) {
        log.debug("[LOCAL_VALIDATOR_AUTO_CONFIG] - Use default MessageSource for hibernate validation");
        // Connect the validation to MessageSource to get error messages from message bundles
        // When a validation error occurs, Spring looks for the error message from the .properties files provided
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
