package com.dct.config.autoconfig;

import com.dct.model.config.properties.InterceptorProps;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;
import java.util.Objects;

import static com.dct.model.constants.ActivateStatus.ENABLED_VALUE;

@AutoConfiguration
@EnableConfigurationProperties(InterceptorProps.class)
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_INTERCEPTOR_CONFIG, havingValue = ENABLED_VALUE)
public class InterceptorAutoConfiguration implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(InterceptorAutoConfiguration.class);
    private final ApplicationContext applicationContext;
    private final InterceptorProps interceptorProps;

    public InterceptorAutoConfiguration(ApplicationContext applicationContext, InterceptorProps interceptorProps) {
        this.applicationContext = applicationContext;
        this.interceptorProps = interceptorProps;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        log.debug("[INTERCEPTOR_AUTO_CONFIG] - Registering handler interceptors");

        interceptorProps.getChain().forEach(interceptorConfig -> {
            log.debug("[INTERCEPTOR_AUTO_CONFIG] - Add interceptor: {}", interceptorConfig.getName().getName());
            HandlerInterceptor interceptor = getInterceptorInstance(interceptorConfig.getName());
            InterceptorRegistration interceptorRegistration = registry.addInterceptor(interceptor);

            if (Objects.nonNull(interceptorConfig.getIncludedPatterns())) {
                interceptorRegistration.addPathPatterns(interceptorConfig.getIncludedPatterns());
            }

            if (Objects.nonNull(interceptorConfig.getExcludedPatterns())) {
                interceptorRegistration.excludePathPatterns(interceptorConfig.getExcludedPatterns());
            }
        });
    }

    private HandlerInterceptor getInterceptorInstance(Class<?> clazz) {
        // If the interceptor is a Spring bean, get it from the context
        Map<String, ?> beans = applicationContext.getBeansOfType(clazz);

        if (!beans.isEmpty()) {
            return (HandlerInterceptor) beans.values().iterator().next();
        }

        // If it is not Spring bean, create instance yourself
        try {
            return (HandlerInterceptor) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize interceptor: " + clazz.getName(), e);
        }
    }
}
