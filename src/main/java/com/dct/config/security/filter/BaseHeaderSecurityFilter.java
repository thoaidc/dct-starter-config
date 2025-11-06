package com.dct.config.security.filter;

import com.dct.model.common.SecurityUtils;
import com.dct.model.config.properties.SecurityProps;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseSecurityConstants;
import com.dct.model.dto.auth.BaseUserDTO;
import com.dct.model.exception.BaseAuthenticationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseHeaderSecurityFilter extends BaseAuthenticationFilter{

    private static final Logger log = LoggerFactory.getLogger(BaseHeaderSecurityFilter.class);
    private static final String ENTITY_NAME = "com.dct.config.security.filter.BaseHeaderSecurityFilter";
    private final SecurityProps securityProps;

    public BaseHeaderSecurityFilter(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Override
    protected boolean shouldAuthenticateRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("[HEADER_SECURITY_FORWARD_FILTER] - Filtering {}: {}", request.getMethod(), requestURI);
        return SecurityUtils.checkIfAuthenticationRequired(requestURI, securityProps.getPublicRequestPatterns());
    }

    @Override
    protected void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String userId = request.getHeader(BaseSecurityConstants.HEADER.USER_ID);
        String username = request.getHeader(BaseSecurityConstants.HEADER.USER_NAME);
        String authorities = request.getHeader(BaseSecurityConstants.HEADER.USER_AUTHORITIES);
        log.info("[RESOLVE_HEADER_FORWARDED] - userId: {}, username: {}", userId, username);

        Collection<SimpleGrantedAuthority> userAuthorities = Arrays
                .stream(Optional.ofNullable(authorities).orElse("").split(","))
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        BaseUserDTO userDTO = BaseUserDTO.userBuilder()
                .withId(Integer.parseInt(userId))
                .withUsername(username)
                .withAuthorities(userAuthorities)
                .build();

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDTO, username, userAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("[AUTHENTICATE_HEADER_ERROR] - Could not set authentication from header forwarded: {}", e.getMessage());
            throw BaseAuthenticationException.builder()
                    .entityName(ENTITY_NAME)
                    .errorKey(BaseExceptionConstants.UNAUTHORIZED)
                    .build();
        }
    }
}
