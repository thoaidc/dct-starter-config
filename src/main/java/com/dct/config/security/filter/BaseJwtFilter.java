package com.dct.config.security.filter;

import com.dct.model.common.SecurityUtils;
import com.dct.model.config.properties.SecurityProps;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseJwtFilter extends BaseAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(BaseJwtFilter.class);
    private final String[] publicRequestPatterns;
    private final BaseJwtProvider jwtProvider;

    public BaseJwtFilter(SecurityProps securityProps, BaseJwtProvider jwtProvider) {
        this.publicRequestPatterns = securityProps.getPublicRequestPatterns();
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldAuthenticateRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("[JWT_FILTER] - Filtering {}: {}", request.getMethod(), requestURI);
        return SecurityUtils.checkIfAuthenticationRequired(requestURI, publicRequestPatterns);
    }

    @Override
    protected void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        Authentication authentication = this.jwtProvider.validateAccessToken(SecurityUtils.retrieveToken(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
