package com.dct.config.security.handler;

import com.dct.config.common.Common;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.constants.BaseExceptionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class DefaultBaseAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(DefaultBaseAuthenticationEntryPoint.class);
    private final MessageTranslationUtils messageTranslationUtils;

    public DefaultBaseAuthenticationEntryPoint(MessageTranslationUtils messageTranslationUtils) {
        this.messageTranslationUtils = messageTranslationUtils;
    }

    /**
     * Directly responds to the client in case of authentication errors without passing the request to further filters <p>
     * In this case, a custom JSON response is sent back <p>
     * You can add additional business logic here, such as sending a redirect or logging failed login attempts, etc.
     *
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param e that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        log.error("[UNAUTHORIZED_ERROR] - message: {}, url: {}", e.getMessage(), request.getRequestURL());
        String message = messageTranslationUtils.getMessageI18n(BaseExceptionConstants.UNAUTHORIZED);
        Common.handleUnauthorizedError(response, message);
    }
}
