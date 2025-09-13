package com.dct.config.security.handler;

import com.dct.model.common.JsonUtils;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.constants.BaseHttpStatusConstants;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.dto.response.BaseResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultBaseAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultBaseAccessDeniedHandler.class);
    private final MessageTranslationUtils messageTranslationUtils;

    public DefaultBaseAccessDeniedHandler(MessageTranslationUtils messageTranslationUtils) {
        this.messageTranslationUtils = messageTranslationUtils;
    }

    /**
     * Directly responds to the client when they lack sufficient access rights,
     * without passing the request to further filters <p>
     * In this case, a custom JSON response is sent back <p>
     * You can add additional business logic here, such as sending a redirect or other necessary actions
     *
     * @param request that resulted in an <code>AccessDeniedException</code>
     * @param response so that the user agent can be advised of the failure
     * @param exception that caused the invocation
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        log.error("[ACCESS_DENIED_ERROR] - message: {}, url: {}", exception.getMessage(), request.getRequestURL());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Convert response body to JSON
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(BaseHttpStatusConstants.FORBIDDEN);
        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.FORBIDDEN)
                .success(Boolean.FALSE)
                .message(messageTranslationUtils.getMessageI18n(BaseExceptionConstants.FORBIDDEN))
                .build();
        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
