package com.dct.base.security.jwt;

import com.dct.base.common.JsonUtils;
import com.dct.base.constants.HttpStatusConstants;
import com.dct.base.constants.SecurityConstants;
import com.dct.base.dto.response.BaseResponseDTO;
import com.dct.base.exception.BaseException;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class BaseJwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseJwtFilter.class);
    private static final String ENTITY_NAME = "BaseJwtFilter";

    protected abstract boolean shouldAuthenticateRequest(HttpServletRequest request);
    protected abstract void resolveToken(String token);

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (shouldAuthenticateRequest(request)) {
            try {
                resolveToken(retrieveToken(request));
            } catch (BaseException exception) {
                handleAuthException(response, exception);
                return;
            } catch (Exception exception) {
                log.error("JwtFilter unable to process response for: {}", exception.getClass().getName(), exception);
                throw exception;
            }
        }

        filterChain.doFilter(request, response);
    }

    protected String retrieveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String bearerToken = null;

        if (Objects.nonNull(cookies)) {
            bearerToken = Arrays.stream(cookies)
                    .filter(cookie -> SecurityConstants.COOKIES.HTTP_ONLY_COOKIE_ACCESS_TOKEN.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        if (!StringUtils.hasText(bearerToken))
            bearerToken = request.getHeader(SecurityConstants.HEADER.AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(bearerToken))
            bearerToken = request.getHeader(SecurityConstants.HEADER.AUTHORIZATION_GATEWAY_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.HEADER.TOKEN_TYPE))
            return bearerToken.substring(7);

        return bearerToken;
    }

    protected void handleAuthException(HttpServletResponse response, BaseException exception) throws IOException {
        log.error("[{}] - Handling exception {}", ENTITY_NAME, exception.getClass().getName(), exception);
        response.setStatus(HttpStatusConstants.UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(HttpStatusConstants.UNAUTHORIZED)
                .success(HttpStatusConstants.STATUS.FAILED)
                .message(exception.getLocalizedMessage())
                .build();

        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
