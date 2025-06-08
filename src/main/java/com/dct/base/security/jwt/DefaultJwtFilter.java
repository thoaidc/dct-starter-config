package com.dct.base.security.jwt;

import com.dct.base.common.JsonUtils;
import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.constants.BaseHttpStatusConstants;
import com.dct.base.dto.response.BaseResponseDTO;
import com.dct.base.exception.BaseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultJwtFilter extends BaseJwtFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultJwtFilter.class);
    private static final String ENTITY_NAME = "DefaultJwtFilter";
    private final MessageTranslationUtils messageTranslationUtils;
    private final BaseJwtProvider jwtProvider;

    public DefaultJwtFilter(BaseJwtProvider jwtProvider, MessageTranslationUtils messageTranslationUtils) {
        this.jwtProvider = jwtProvider;
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    protected boolean shouldAuthenticateRequest(HttpServletRequest request) {
        return true;
    }

    @Override
    protected void resolveToken(String token) {
        Authentication authentication = this.jwtProvider.validateToken(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void handleAuthException(HttpServletResponse response, BaseException exception) throws IOException {
        log.error("[{}] - Handling exception {}", ENTITY_NAME, exception.getClass().getName(), exception);
        response.setStatus(BaseHttpStatusConstants.UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.UNAUTHORIZED)
                .success(BaseHttpStatusConstants.STATUS.FAILED)
                .message(messageTranslationUtils.getMessageI18n(exception.getErrorKey(), exception.getArgs()))
                .build();

        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
