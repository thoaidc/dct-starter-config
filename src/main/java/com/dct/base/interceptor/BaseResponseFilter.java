package com.dct.base.interceptor;

import com.dct.base.dto.response.BaseResponseDTO;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@link ResponseBodyAdvice} is an interface that allows you to intervene with the response data <p>
 * Executed after {@link HandlerInterceptor#postHandle} and before the actual data is written into the response body
 *
 * @author thoaidc
 */
@ControllerAdvice
public abstract class BaseResponseFilter implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(BaseResponseFilter.class);
    private static final String ENTITY_NAME = "BaseResponseFilter";

    /**
     * Check if the response is a {@link BaseResponseDTO} or {@link ResponseEntity}<{@link BaseResponseDTO}> <p>
     * If true, the response will be processed by {@link BaseResponseFilter#beforeBodyWrite}
     *
     * @param returnType the return type
     * @param converter the selected converter type
     * @return true if response type is supported by this filter
     */
    @Override
    public boolean supports(@Nonnull MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converter) {
        log.debug("[{}] - Response will be processed by: {}", ENTITY_NAME, converter.getSimpleName());
        return isSupport(returnType, converter);
    }

    /**
     * Override the response body to add internationalization (I18n) messages before it is returned
     *
     * @param body the body of HTTP request to be written
     * @param returnType the return type of the controller method
     * @param selectedContentType Media type requested by the client, usually application/json, application/xml, etc
     * @param selectedConverterType Type of converter that Spring will use to convert the response body (to JSON, XML)
     * @param request the current request
     * @param response the current response
     * @return ResponseEntity after modification
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @Nullable MethodParameter returnType,
                                  @Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request,
                                  @Nullable ServerHttpResponse response) {
        return writeBody(body, request, response);
    }

    protected abstract boolean isSupport(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converter);

    protected abstract Object writeBody(Object body, ServerHttpRequest request, ServerHttpResponse response);
}
