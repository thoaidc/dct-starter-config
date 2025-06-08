package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.constants.BaseSecurityConstants;
import com.dct.base.interceptor.BaseResponseFilter;
import com.dct.base.interceptor.DefaultBaseResponseFilter;
import com.dct.base.security.config.BaseCorsRequestMatchersConfig;
import com.dct.base.security.config.DefaultBaseCorsRequestMatchersConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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

    @Bean
    @ConditionalOnMissingBean(BaseCorsRequestMatchersConfig.class)
    public BaseCorsRequestMatchersConfig defaultBaseCorsRequestMatchersConfig() {
        log.debug("[{}] - Use default CORS request matchers configuration", ENTITY_NAME);
        return new DefaultBaseCorsRequestMatchersConfig();
    }

    /**
     * Configures the CORS (Cross-Origin Resource Sharing) filter in the application <p>
     * CORS is a security mechanism that allows or denies requests between different origins <p>
     * View the details of the permissions or restrictions in {@link BaseSecurityConstants.CORS}
     */
    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter defaultCorsFilter(BaseCorsRequestMatchersConfig corsRequestMatchersConfig) {
        log.debug("[{}] - Auto configure default CORS filter", ENTITY_NAME);
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowedOriginPatterns(corsRequestMatchersConfig.getAllowedOriginPatterns());
        config.setAllowedHeaders(corsRequestMatchersConfig.getAllowedHeaders());
        config.setAllowedMethods(corsRequestMatchersConfig.getAllowedMethods());
        config.setAllowCredentials(corsRequestMatchersConfig.isAllowCredentials());

        for (String pattern : corsRequestMatchersConfig.applyFor()) {
            source.registerCorsConfiguration(pattern, config);
        }

        return new CorsFilter(source);
    }
}
