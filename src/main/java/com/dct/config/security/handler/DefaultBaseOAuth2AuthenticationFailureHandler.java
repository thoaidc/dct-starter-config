package com.dct.config.security.handler;

import com.dct.config.common.Common;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.constants.BaseExceptionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

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
        String message = messageTranslationUtils.getMessageI18n(BaseExceptionConstants.UNAUTHORIZED);
        Common.handleUnauthorizedError(response, message);
    }
}
