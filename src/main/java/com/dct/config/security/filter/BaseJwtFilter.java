package com.dct.config.security.filter;

import com.dct.model.common.SecurityUtils;
import com.dct.model.config.properties.SecurityProps;
import com.dct.model.constants.BaseSecurityConstants;
import com.dct.model.security.BaseJwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

public class BaseJwtFilter extends BaseAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseJwtFilter.class);
    private final SecurityProps securityProps;
    private final BaseJwtProvider jwtProvider;

    public BaseJwtFilter(SecurityProps securityProps,
                         BaseJwtProvider jwtProvider) {
        this.securityProps = securityProps;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldAuthenticateRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("[JWT_FILTER] - Filtering {}: {}", request.getMethod(), requestURI);
        return SecurityUtils.checkIfAuthenticationRequired(requestURI, securityProps.getPublicRequestPatterns());
    }

    @Override
    protected void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String token = retrieveToken(request);
        Authentication authentication = this.jwtProvider.parseToken(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected String retrieveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String bearerToken = null;

        if (Objects.nonNull(cookies)) {
            bearerToken = Arrays.stream(cookies)
                    .filter(cookie -> BaseSecurityConstants.COOKIES.HTTP_ONLY_COOKIE_ACCESS_TOKEN.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        if (!StringUtils.hasText(bearerToken))
            bearerToken = request.getHeader(BaseSecurityConstants.HEADER.AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(bearerToken))
            bearerToken = request.getHeader(BaseSecurityConstants.HEADER.AUTHORIZATION_GATEWAY_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BaseSecurityConstants.HEADER.TOKEN_TYPE))
            return bearerToken.substring(7);

        return bearerToken;
    }
}
