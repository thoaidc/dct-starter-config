package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.constants.BaseSecurityConstants;
import com.dct.base.interceptor.BaseResponseFilter;
import com.dct.base.interceptor.DefaultBaseResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

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

    /**
     * Configures the CORS (Cross-Origin Resource Sharing) filter in the application <p>
     * CORS is a security mechanism that allows or denies requests between different origins <p>
     * View the details of the permissions or restrictions in {@link BaseSecurityConstants.CORS}
     */
    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter defaultCorsFilter() {
        log.debug("[{}] - Auto configure default CORS filter", ENTITY_NAME);
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(BaseSecurityConstants.CORS.ALLOWED_ORIGIN_PATTERNS));
        config.setAllowedHeaders(List.of(BaseSecurityConstants.CORS.ALLOWED_HEADERS));
        config.setAllowedMethods(List.of(BaseSecurityConstants.CORS.ALLOWED_REQUEST_METHODS));
        config.setAllowCredentials(BaseSecurityConstants.CORS.ALLOW_CREDENTIALS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(BaseSecurityConstants.CORS.APPLY_FOR, config);

        return new CorsFilter(source);
    }
}
