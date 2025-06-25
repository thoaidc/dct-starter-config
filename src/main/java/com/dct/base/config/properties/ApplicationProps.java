package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.constants.FeatureStatus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = BasePropertiesConstants.APP_CONFIG)
public class ApplicationProps {

    private FeatureStatus jpaAuditing;
    private FeatureStatus datasource;
    private FeatureStatus exceptionHandling;
    private FeatureStatus httpClient;
}
