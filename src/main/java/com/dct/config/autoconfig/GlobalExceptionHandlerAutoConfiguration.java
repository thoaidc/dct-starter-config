package com.dct.config.autoconfig;

import com.dct.config.exception.BaseExceptionHandler;
import com.dct.config.exception.DefaultBaseExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class GlobalExceptionHandlerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandlerAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(BaseExceptionHandler.class)
    public BaseExceptionHandler defaultBaseExceptionHandler() {
        log.debug("[EXCEPTION_HANDLER_AUTO_CONFIG] - Use default global exception handler");
        return new DefaultBaseExceptionHandler();
    }
}
