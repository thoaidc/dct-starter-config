package com.dct.config.interceptor;

import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.dto.response.BaseResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

public class DefaultBaseResponseFilter extends BaseResponseFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultBaseResponseFilter.class);
    private final MessageTranslationUtils messageUtils;

    public DefaultBaseResponseFilter(MessageTranslationUtils messageUtils) {
        super();
        this.messageUtils = messageUtils;
    }

    @Override
    protected boolean isSupport(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converter) {
        log.debug("[I18N_SUPPORT_RESPONSE_TYPE] - Supports types: `BaseResponseDTO`, `ResponseEntity`");
        boolean isBaseResponseDTOType = BaseResponseDTO.class.isAssignableFrom(returnType.getParameterType());
        boolean isBasicResponseEntity = ResponseEntity.class.isAssignableFrom(returnType.getParameterType());
        return isBaseResponseDTOType || isBasicResponseEntity;
    }

    @Override
    protected Object writeBody(Object body, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof BaseResponseDTO) {
            return messageUtils.setResponseMessageI18n((BaseResponseDTO) body);
        }

        if (body instanceof ResponseEntity<?> responseEntity) {
            Object responseBody = responseEntity.getBody();

            if (responseBody instanceof BaseResponseDTO) {
                BaseResponseDTO responseDTO = messageUtils.setResponseMessageI18n((BaseResponseDTO) responseBody);
                return new ResponseEntity<>(responseDTO, responseEntity.getHeaders(), responseEntity.getStatusCode());
            }
        }

        return body;
    }
}
