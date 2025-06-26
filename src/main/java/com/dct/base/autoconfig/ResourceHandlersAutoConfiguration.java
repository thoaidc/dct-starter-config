package com.dct.base.autoconfig;

import com.dct.base.config.properties.ResourceProps;
import com.dct.base.constants.ActivateStatus;
import com.dct.base.constants.BaseCommonConstants;
import com.dct.base.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_RESOURCE, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableConfigurationProperties(ResourceProps.class)
public class ResourceHandlersAutoConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ResourceHandlersAutoConfiguration.class);
    private static final String ENTITY_NAME = "ResourceHandlersAutoConfiguration";
    private final ResourceProps resourceProps;

    public ResourceHandlersAutoConfiguration(ResourceProps resourceProps) {
        this.resourceProps = resourceProps;
    }

    /**
     * The class configures Spring to serve static resources
     * from directories on the classpath (e.g. static, content, i18n)<p>
     * The static resource paths defined in {@link BaseCommonConstants.STATIC_RESOURCES#PATHS}
     * will be mapped to the directories listed in {@link BaseCommonConstants.STATIC_RESOURCES#LOCATIONS} <p>
     * When a request comes in for static resources such as .js, .css, .svg, etc.,
     * Spring will look for the files in the configured directories and return the corresponding content
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.debug("[{}] - Auto configure resources handler", ENTITY_NAME);
        String[] STATIC_RESOURCE_PATHS = BaseCommonConstants.STATIC_RESOURCES.PATHS;
        ResourceHandlerRegistration resourceHandler = registry.addResourceHandler(STATIC_RESOURCE_PATHS);
        resourceHandler.addResourceLocations(BaseCommonConstants.STATIC_RESOURCES.LOCATIONS); // For static files
    }
}
