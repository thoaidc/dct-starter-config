package com.dct.config.autoconfig;

import com.dct.config.interceptor.BaseResponseFilter;
import com.dct.config.interceptor.DefaultBaseResponseFilter;
import com.dct.model.common.MessageTranslationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ResponseTranslationAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ResponseTranslationAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(BaseResponseFilter.class)
    public BaseResponseFilter defaultBaseResponseFilter(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[RESPONSE_FILTER_AUTO_CONFIG] - Use default base response filter");
        return new DefaultBaseResponseFilter(messageTranslationUtils);
    }
}
