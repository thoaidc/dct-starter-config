package com.dct.config.autoconfig;

import com.dct.config.interceptor.BaseFeignAuthenticationRequestFilter;
import com.dct.config.interceptor.DefaultFeignAuthenticationRequestFilter;
import com.dct.model.config.properties.SecurityProps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FeignAuthenticationRequestAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FeignAuthenticationRequestAutoConfiguration.class);
    private final SecurityProps securityProps;

    public FeignAuthenticationRequestAutoConfiguration(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Bean
    @ConditionalOnMissingBean(BaseFeignAuthenticationRequestFilter.class)
    public BaseFeignAuthenticationRequestFilter defaultFeignSecurityHeaderInterceptor() {
        log.debug("[FEIGN_SECURITY_AUTO_CONFIG] - Use default feign header security interceptor");
        return new DefaultFeignAuthenticationRequestFilter(securityProps);
    }
}
