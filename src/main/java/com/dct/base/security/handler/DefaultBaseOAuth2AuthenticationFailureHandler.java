package com.dct.base.security.handler;

import com.dct.base.common.JsonUtils;
import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.constants.BaseExceptionConstants;
import com.dct.base.constants.BaseHttpStatusConstants;
import com.dct.base.dto.response.BaseResponseDTO;

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
    private static final String ENTITY_NAME = "DefaultBaseOAuth2AuthenticationFailureHandler";
    private final MessageTranslationUtils messageTranslationUtils;

    public DefaultBaseOAuth2AuthenticationFailureHandler(MessageTranslationUtils messageTranslationUtils) {
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException e) throws IOException {
        log.debug("[{}] - Authentication via OAuth2 failed. {}", ENTITY_NAME, e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Convert response body to JSON
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(BaseHttpStatusConstants.UNAUTHORIZED);

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
            .code(BaseHttpStatusConstants.UNAUTHORIZED)
            .success(BaseHttpStatusConstants.STATUS.FAILED)
            .message(messageTranslationUtils.getMessageI18n(BaseExceptionConstants.OAUTH2_AUTHORIZATION_CODE_EXCEPTION))
            .build();

        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
