package com.dct.config.common;

import com.dct.model.autoconfig.DataConverterAutoConfiguration;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.exception.BaseBadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class HttpClientUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final ObjectMapper objectMapper = DataConverterAutoConfiguration.buildObjectMapper();
    private static final String ENTITY_NAME = "com.dct.config.common.HttpClientUtils";

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RestTemplate restTemplate = new RestTemplate();
        private String url;
        private HttpMethod method;
        private Map<String, List<String>> headers;
        private Map<String, String> params;
        private Object body;

        public Builder restTemplate(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public <T> T execute(Class<T> responseType) {
            HttpHeaders httpHeaders = HttpClientUtils.initHeaders();
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);

            if (Objects.nonNull(headers)) {
                httpHeaders.putAll(headers);
            }

            if (Objects.nonNull(params)) {
                params.forEach(uriBuilder::queryParam);
            }

            try {
                URI uri = uriBuilder.build(Boolean.TRUE).toUri();
                HttpEntity<?> httpEntity = HttpClientUtils.initHttpEntity(httpHeaders, body);
                ResponseEntity<?> response = restTemplate.exchange(uri, method, httpEntity, responseType);
                return objectMapper.convertValue(response.getBody(), responseType);
            } catch (Exception e) {
                log.error("CALL_HTTP_ERROR: url: {}, method: {}, request: {}. {}", url, method, body, e.getMessage());
                return null;
            }
        }
    }

    public static HttpEntity<?> initHttpEntity(HttpHeaders headers, Object requestBody) {
        HttpEntity<?> request;

        try {
            String json = Objects.nonNull(requestBody) ? objectMapper.writeValueAsString(requestBody) : null;
            request = new HttpEntity<>(json, headers);
        } catch (JsonProcessingException e) {
            log.error("PARSE_HTTP_REQUEST_ERROR: request: {}, {}", requestBody, e.getMessage());
            throw new BaseBadRequestAlertException(ENTITY_NAME, BaseExceptionConstants.DATA_INVALID);
        }

        return request;
    }

    public static HttpHeaders initHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 Firefox/26.0");
        return headers;
    }

    public static HttpHeaders initHeaders(Map<String, List<String>> headerParams) {
        HttpHeaders headers = initHeaders();
        headers.putAll(headerParams);
        return headers;
    }
}
