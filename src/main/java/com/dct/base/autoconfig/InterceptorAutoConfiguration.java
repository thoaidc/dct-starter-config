package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.interceptor.BaseResponseFilter;
import com.dct.base.interceptor.DefaultBaseResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class InterceptorAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(InterceptorAutoConfiguration.class);
    private static final String ENTITY_NAME = "InterceptorAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(BaseResponseFilter.class)
    public BaseResponseFilter defaultBaseResponseFilter(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[{}] - Auto configure default base response filter", ENTITY_NAME);
        return new DefaultBaseResponseFilter(messageTranslationUtils);
    }
}
