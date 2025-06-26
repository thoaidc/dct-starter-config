package com.dct.base.autoconfig;

import com.dct.base.constants.BasePropertiesConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static com.dct.base.constants.ActivateStatus.ENABLED_VALUE;

/**
 * Create beans to handle the conversion of values between objects and between objects and JSON
 * @author thoaidc
 */
@AutoConfiguration
public class DataConverterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataConverterAutoConfiguration.class);
    private static final String ENTITY_NAME = "DataConverterAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_OBJECT_MAPPER, havingValue = ENABLED_VALUE)
    public ObjectMapper objectMapper() {
        log.debug("[{}] - Auto configure default object mapper", ENTITY_NAME);
        ObjectMapper objectMapper = new ObjectMapper();

        // Avoid errors when encountering undefined properties in JSON that are not present in the Java class being converted
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Do not serialize (convert to JSON) fields with empty or null values
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        objectMapper.registerModule(new JavaTimeModule()); // To support Instant datatype of Java 8
        objectMapper.registerModule(new Jdk8Module());

        return objectMapper;
    }
}
