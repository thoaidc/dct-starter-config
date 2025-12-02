package com.dct.config.autoconfig;

import com.dct.model.config.properties.KafkaProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {
    // Additional config here
}
