package com.dct.base.autoconfig;

import com.dct.model.config.properties.ResourceProps;
import com.dct.model.config.properties.ResourceProps.StaticResource;
import com.dct.model.config.properties.ResourceProps.UploadResource;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BaseCommonConstants;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_RESOURCE, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableConfigurationProperties(ResourceProps.class)
public class ResourceHandlersAutoConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ResourceHandlersAutoConfiguration.class);
    private static final String ENTITY_NAME = "ResourceHandlersAutoConfiguration";
    private final ResourceProps resourceProps;

    public ResourceHandlersAutoConfiguration(ResourceProps resourceProps) {
        this.resourceProps = Optional.ofNullable(resourceProps).orElse(new ResourceProps());
    }

    /**
     * The class configures Spring to serve static resources
     * from directories on the classpath (e.g. static, content, i18n)<p>
     * The static resource paths defined in PATTERNS
     * will be mapped to the directories listed in LOCATIONS <p>
     * When a request comes in for static resources such as .js, .css, .svg, etc.,
     * Spring will look for the files in the configured directories and return the corresponding content
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.debug("[{}] - Auto configure default resources handler", ENTITY_NAME);
        StaticResource staticConfig = Optional.ofNullable(resourceProps.getStaticResource()).orElse(new StaticResource());
        UploadResource uploadConfig = Optional.ofNullable(resourceProps.getUploadResource()).orElse(new UploadResource());

        final String[] STATIC_PATTERNS = Optional.ofNullable(staticConfig.getPatterns())
                .orElse(BaseCommonConstants.STATIC_RESOURCES.DEFAULT_PATTERNS);

        final String[] STATIC_LOCATIONS = Optional.ofNullable(staticConfig.getLocations())
                .orElse(BaseCommonConstants.STATIC_RESOURCES.DEFAULT_LOCATIONS);

        final String[] UPLOAD_PATTERNS = Optional.ofNullable(uploadConfig.getPatterns())
                .orElse(BaseCommonConstants.UPLOAD_RESOURCES.DEFAULT_PATTERNS);

        final String[] UPLOAD_LOCATIONS = Optional.ofNullable(uploadConfig.getLocations())
                .orElse(BaseCommonConstants.UPLOAD_RESOURCES.DEFAULT_LOCATIONS);

        ResourceHandlerRegistration staticResourcesHandler = registry.addResourceHandler(STATIC_PATTERNS);
        ResourceHandlerRegistration uploadResourcesHandler = registry.addResourceHandler(UPLOAD_PATTERNS);
        staticResourcesHandler.addResourceLocations(STATIC_LOCATIONS);
        uploadResourcesHandler.addResourceLocations(UPLOAD_LOCATIONS);
    }
}
