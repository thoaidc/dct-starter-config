package com.dct.config.interceptor;

import com.dct.model.common.SecurityUtils;
import com.dct.model.config.properties.SecurityProps;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BaseSecurityConstants;
import com.dct.model.dto.auth.BaseUserDTO;
import com.dct.model.exception.BaseAuthenticationException;

import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link BaseFeignAuthenticationRequestFilter} that forwards
 * the current authenticated user's details to downstream services via Feign request headers.
 *
 * <p>This filter is automatically applied to all Feign client requests (if registered as a Spring bean)
 * and is responsible for:
 * <ul>
 *   <li>Determining if authentication is required for the request URL, based on
 *       {@link SecurityProps#getPublicRequestPatterns()}
 *   </li>
 *   <li>Extracting the current {@link Authentication} object from the Spring Security context</li>
 *   <li>Reading user details (userId, username, authorities,...) from {@link BaseUserDTO}</li>
 *   <li>Injecting these details as custom headers into the outgoing Feign request</li>
 * </ul>
 *
 * <p>If authentication is missing or invalid, this filter throws a {@link BaseAuthenticationException}
 * with an {@code UNAUTHORIZED} error key
 *
 * <p>Example of injected headers:
 * <pre>
 *   USER_ID: 456
 *   USER_NAME: haha
 * </pre>
 *
 * <p>Usage:
 * <ul>
 *   <li>Ensure Spring Security is configured and a valid {@link Authentication} is present
 *       in the {@link SecurityContextHolder}
 *   </li>
 *   <li>Register this class as a Spring bean (e.g., {@code @Component} or via {@code @Bean})</li>
 *   <li>Any Feign request matching a protected URL pattern will automatically include
 *       the user's security context in the headers
 *   </li>
 * </ul>
 *
 * @author thoaidc
 */
public class DefaultFeignAuthenticationRequestFilter extends BaseFeignAuthenticationRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(DefaultFeignAuthenticationRequestFilter.class);
    private static final String ENTITY_NAME = "com.dct.config.interceptor.DefaultFeignAuthenticationRequestFilter";
    private final SecurityProps securityProps;

    public DefaultFeignAuthenticationRequestFilter(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    /**
     * Handles the injection of authentication details into the Feign request headers.
     *
     * @param requestTemplate the Feign {@link RequestTemplate} to customize
     * @throws BaseAuthenticationException if authentication is missing or invalid
     */
    @Override
    public void handle(RequestTemplate requestTemplate) {
        String targetUrl = requestTemplate.feignTarget().url();
        String requestUrl = URI.create(targetUrl).getPath() + requestTemplate.path();
        log.info("[FEIGN_REQUEST_FORWARDED] - Filtering: {}", requestUrl);

        if (SecurityUtils.checkIfAuthenticationRequired(requestUrl, securityProps.getExternalServicePublicRequestPatterns())) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                BaseUserDTO userDTO = (BaseUserDTO) authentication.getPrincipal();
                Integer userId = userDTO.getId();
                String username = userDTO.getUsername();
                Set<String> authorities = Optional.ofNullable(userDTO.getAuthorities())
                        .orElse(Collections.emptySet())
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                log.info("[FEIGN_REQUEST_FORWARDED] - userId: {}, username: {}", userId, username);
                requestTemplate.header(BaseSecurityConstants.HEADER.USER_ID, String.valueOf(userId));
                requestTemplate.header(BaseSecurityConstants.HEADER.USER_NAME, username);
                requestTemplate.header(BaseSecurityConstants.HEADER.USER_AUTHORITIES, String.join(",", authorities));
            } catch (Exception e) {
                log.error("[FEIGN_REQUEST_FORWARDED_ERROR] - Missing or invalid authentication: {}", e.getMessage());
                throw BaseAuthenticationException.builder()
                        .entityName(ENTITY_NAME)
                        .errorKey(BaseExceptionConstants.UNAUTHORIZED)
                        .build();
            }
        }
    }
}
