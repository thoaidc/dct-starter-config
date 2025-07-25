package com.dct.config.security.config;

import com.dct.config.common.SecurityUtils;
import com.dct.config.security.jwt.BaseJwtFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Objects;

public abstract class BaseSecurityFilterChainConfig {

    private static final Logger log = LoggerFactory.getLogger(BaseSecurityFilterChainConfig.class);
    private static final String ENTITY_NAME = "BaseSecurityFilterChainConfig";
    private final BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig;
    private final CorsFilter corsFilter;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private BaseJwtFilter jwtFilter;

    public BaseSecurityFilterChainConfig(CorsFilter corsFilter,
                                         BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                         AccessDeniedHandler accessDeniedHandler,
                                         AuthenticationEntryPoint authenticationEntryPoint) {
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
        this.corsFilter = corsFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    protected BaseSecurityFilterChainConfig(CorsFilter corsFilter,
                                            BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                            AccessDeniedHandler accessDeniedHandler,
                                            AuthenticationEntryPoint authenticationEntryPoint,
                                            BaseJwtFilter jwtFilter) {
        this.corsFilter = corsFilter;
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtFilter = jwtFilter;
    }

    public void cors(HttpSecurity http) throws Exception {
        // Because of using JWT, CSRF is not required
        log.debug("[{}] - Use default cors and csrf configuration: CSRF is disabled", ENTITY_NAME);
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
    }

    public void addFilters(HttpSecurity http) {
        log.debug("[{}] - Use default filters orders configuration", ENTITY_NAME);
        http.addFilterAfter(corsFilter, HeaderWriterFilter.class);

        if (Objects.nonNull(jwtFilter)) {
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    public void exceptionHandlers(HttpSecurity http) throws Exception {
        log.debug("[{}] - Use default exception handlers configuration", ENTITY_NAME);
        http.exceptionHandling(handler -> handler
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );
    }

    public void headersSecurity(HttpSecurity http) throws Exception {
        log.debug("[{}] - Use default headers security configuration", ENTITY_NAME);
        http.headers(header -> header
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            .referrerPolicy(config ->
                config.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
        );
    }

    public void sessionManagementStrategy(HttpSecurity http) throws Exception {
        log.debug("[{}] - Use default session management strategy configuration: STATELESS", ENTITY_NAME);
        http.sessionManagement(sessionManager ->
                sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    public void authorizeHttpRequests(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        String[] publicApiPatterns = securityAuthorizeRequestConfig.getPublicPatterns();
        log.debug("[{}] - Use default authorize http requests configuration: FormLogin is disabled", ENTITY_NAME);
        log.debug("[{}] - Ignore authorize request matchers: {}", ENTITY_NAME, Arrays.toString(publicApiPatterns));
        http.authorizeHttpRequests(registry -> registry
            .requestMatchers(SecurityUtils.convertToMvcMatchers(mvc, publicApiPatterns))
            .permitAll()
            // Used with custom CORS filters in CORS (Cross-Origin Resource Sharing) mechanism
            // The browser will send OPTIONS requests (preflight requests) to check
            // if the server allows access from other sources before send requests such as POST, GET
            .requestMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(AbstractHttpConfigurer::disable);
    }

    @SuppressWarnings("unused")
    public void oauth2(HttpSecurity http) throws Exception {
        // Add logic Oauth2 config here
    }
}
