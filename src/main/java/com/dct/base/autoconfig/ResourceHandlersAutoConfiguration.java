package com.dct.base.autoconfig;

import com.dct.base.config.properties.UploadProps;
import com.dct.base.constants.BaseCommonConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

@AutoConfiguration
public class ResourceHandlersAutoConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ResourceHandlersAutoConfiguration.class);
    private static final String ENTITY_NAME = "ResourceHandlersAutoConfiguration";
    private final UploadProps uploadProps;

    public ResourceHandlersAutoConfiguration(UploadProps uploadProps) {
        this.uploadProps = uploadProps;
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

        // For upload files
        if (Objects.nonNull(uploadProps) && Objects.nonNull(uploadProps.getPaths())) {
            uploadProps.getPaths().forEach(path -> resourceHandler.addResourceLocations("file:" + path));
            log.info("Serve upload resources in: {}", uploadProps.getPaths());
        }
    }
}
