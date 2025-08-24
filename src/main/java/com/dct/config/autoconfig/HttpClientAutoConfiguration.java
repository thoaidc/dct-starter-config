package com.dct.config.autoconfig;

import com.dct.config.interceptor.BaseCircuitBreakerRestTemplateInterceptor;
import com.dct.model.config.properties.CircuitBreakerProps;
import com.dct.model.config.properties.HttpClientProps;
import com.dct.model.constants.BasePropertiesConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.dct.model.constants.ActivateStatus.DISABLED_VALUE;
import static com.dct.model.constants.ActivateStatus.ENABLED_VALUE;

/**
 * Helps the application use functions related to sending and receiving HTTP requests/responses <p>
 * Ex: call API to external system
 *
 * @author thoaidc
 */
@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_REST_TEMPLATE, havingValue = ENABLED_VALUE)
@EnableConfigurationProperties(HttpClientProps.class)
public class HttpClientAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);
    private final ObjectMapper objectMapper;
    private final HttpClientProps httpClientProps;
    private final CircuitBreakerProps circuitBreakerProps;
    private final BaseCircuitBreakerRestTemplateInterceptor circuitBreakerInterceptor;

    public HttpClientAutoConfiguration(ObjectMapper objectMapper,
                                       HttpClientProps httpClientProps,
                                       @Autowired(required = false) CircuitBreakerProps circuitBreakerProps,
                                       @Autowired(required = false)
                                       BaseCircuitBreakerRestTemplateInterceptor circuitBreakerInterceptor) {
        this.objectMapper = objectMapper;
        this.httpClientProps = httpClientProps;
        this.circuitBreakerProps = circuitBreakerProps;
        this.circuitBreakerInterceptor = circuitBreakerInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_CIRCUIT_BREAKER_CONFIG, havingValue = ENABLED_VALUE)
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        log.debug("[CLIENT_HTTP_REQUEST_FACTORY_AUTO_CONFIG] - Use default factory with embedded Circuit Breaker");
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(circuitBreakerProps.getConnectTimeout());
        factory.setReadTimeout(circuitBreakerProps.getReadTimeout());
        return factory;
    }

    @Bean
    @ConditionalOnProperty(
        name = BasePropertiesConstants.ENABLED_CIRCUIT_BREAKER_CONFIG,
        havingValue = DISABLED_VALUE,
        matchIfMissing = true
    )
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory clientHttpRequestFactoryWithoutCircuitBreaker() {
        log.debug("[CLIENT_HTTP_REQUEST_FACTORY_AUTO_CONFIG] - Use default factory without Circuit Breaker");
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(httpClientProps.getDefaultConnectTimeout());
        factory.setReadTimeout(httpClientProps.getDefaultReadTimeout());
        return factory;
    }

    /**
     * Defines a RestTemplate bean with embedded Circuit Breaker <p>
     * Purpose:
     * <ul>
     *     <li>Used when {@link BasePropertiesConstants#ENABLED_CIRCUIT_BREAKER_CONFIG} having value = enabled</li>
     *     <li>Adds CircuitBreaker interceptor to handle failures and retries</li>
     *     <li>Supports sending HTTP requests and mapping JSON responses to Java objects</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_CIRCUIT_BREAKER_CONFIG, havingValue = ENABLED_VALUE)
    public RestTemplate defaultRestTemplateWithCircuitBreaker(ClientHttpRequestFactory clientHttpRequestFactory) {
        log.debug("[REST_TEMPLATE_AUTO_CONFIG] - Use default RestTemplate with embedded Circuit Breaker");
        log.debug("[REST_TEMPLATE_AUTO_CONFIG] - Use CB filter: {}", circuitBreakerInterceptor.getClass().getName());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // Create an HTTP message converter, using JacksonConverter to convert between JSON and Java objects
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(converter);
        restTemplate.getInterceptors().add(circuitBreakerInterceptor);
        return restTemplate;
    }

    /**
     * Defines a RestTemplate bean without Circuit Breaker <p>
     * Purpose:
     * <ul>
     *     <li>Used when {@link BasePropertiesConstants#ENABLED_CIRCUIT_BREAKER_CONFIG} having = disabled or missing</li>
     *     <li>Standard RestTemplate without failure handling interceptors</li>
     *     <li>Supports sending HTTP requests and mapping JSON responses to Java objects</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    @ConditionalOnProperty(
        name = BasePropertiesConstants.ENABLED_CIRCUIT_BREAKER_CONFIG,
        havingValue = DISABLED_VALUE,
        matchIfMissing = true
    )
    public RestTemplate restTemplateWithoutCircuitBreaker(ClientHttpRequestFactory clientHttpRequestFactoryWithoutCB) {
        log.debug("[REST_TEMPLATE_AUTO_CONFIG] - Use default RestTemplate without CircuitBreaker");
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactoryWithoutCB);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }
}
