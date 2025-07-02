package com.dct.base.autoconfig;

import com.dct.model.constants.BasePropertiesConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.dct.model.constants.ActivateStatus.ENABLED_VALUE;

/**
 * Helps the application use functions related to sending and receiving HTTP requests/responses, similar to a client
 * @author thoaidc
 */
@AutoConfiguration
public class HttpClientAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);
    private static final String ENTITY_NAME = "HttpClientAutoConfiguration";
    private final ObjectMapper objectMapper;

    public HttpClientAutoConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * This configuration defines a RestTemplate bean in Spring <p>
     * Purpose: Create a tool that makes sending HTTP requests and handling responses
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_REST_TEMPLATE, havingValue = ENABLED_VALUE)
    public RestTemplate defaultRestTemplate() {
        log.debug("[{}] - Auto configure default RestTemplate for send HTTP request/response in spring", ENTITY_NAME);
        RestTemplate restTemplate = new RestTemplate();
        // Create an HTTP message converter, using JacksonConverter to convert between JSON and Java objects
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }
}
