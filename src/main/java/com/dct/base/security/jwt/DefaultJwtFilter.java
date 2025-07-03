package com.dct.base.security.jwt;

import com.dct.base.security.config.BaseSecurityAuthorizeRequestConfig;
import com.dct.model.common.JsonUtils;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.constants.BaseHttpStatusConstants;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.exception.BaseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class DefaultJwtFilter extends BaseJwtFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultJwtFilter.class);
    private static final String ENTITY_NAME = "DefaultJwtFilter";
    private final BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig;
    private final BaseJwtProvider jwtProvider;
    private MessageTranslationUtils messageTranslationUtils;

    public DefaultJwtFilter(BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                            BaseJwtProvider jwtProvider) {
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
        this.jwtProvider = jwtProvider;
    }

    public DefaultJwtFilter(BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                            BaseJwtProvider jwtProvider,
                            MessageTranslationUtils messageTranslationUtils) {
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
        this.jwtProvider = jwtProvider;
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    protected boolean shouldAuthenticateRequest(HttpServletRequest request) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();
        log.info("[{}] - Filtering {}: {}", ENTITY_NAME, request.getMethod(), requestURI);

        return Arrays.stream(securityAuthorizeRequestConfig.getPublicPatterns())
                .noneMatch(pattern -> antPathMatcher.match(pattern, requestURI));
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
        String message = Objects.nonNull(messageTranslationUtils)
                ? messageTranslationUtils.getMessageI18n(exception.getErrorKey(), exception.getArgs())
                : HttpStatus.UNAUTHORIZED.name();

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.UNAUTHORIZED)
                .success(BaseHttpStatusConstants.STATUS.FAILED)
                .message(message)
                .build();

        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
