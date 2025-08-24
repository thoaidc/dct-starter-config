package com.dct.config.security.handler;

import com.dct.model.common.JsonUtils;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseHttpStatusConstants;
import com.dct.model.dto.response.BaseResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultBaseOAuth2AuthenticationFailureHandler extends BaseOAuth2AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultBaseOAuth2AuthenticationFailureHandler.class);
    private final MessageTranslationUtils messageTranslationUtils;

    public DefaultBaseOAuth2AuthenticationFailureHandler(MessageTranslationUtils messageTranslationUtils) {
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException e) throws IOException {
        log.debug("[OAUTH2_FAILURE_HANDLER] - Authentication via OAuth2 failed. {}", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Convert response body to JSON
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(BaseHttpStatusConstants.UNAUTHORIZED);

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
            .code(BaseHttpStatusConstants.UNAUTHORIZED)
            .success(Boolean.FALSE)
            .message(messageTranslationUtils.getMessageI18n(BaseExceptionConstants.OAUTH2_AUTHORIZATION_CODE_EXCEPTION))
            .build();

        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
