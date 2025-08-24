package com.dct.config.security.config;

import com.dct.config.security.filter.BaseAuthenticationFilter;
import com.dct.model.common.SecurityUtils;
import com.dct.model.config.properties.SecurityProps;

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
    private final SecurityProps securityProps;
    private final CorsFilter corsFilter;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final BaseAuthenticationFilter authenticationFilter;

    protected BaseSecurityFilterChainConfig(SecurityProps securityProps,
                                            CorsFilter corsFilter,
                                            AccessDeniedHandler accessDeniedHandler,
                                            AuthenticationEntryPoint authenticationEntryPoint,
                                            BaseAuthenticationFilter baseAuthenticationFilter) {
        this.securityProps = securityProps;
        this.corsFilter = corsFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = baseAuthenticationFilter;
    }

    public void cors(HttpSecurity http) throws Exception {
        // Because of using JWT, CSRF is not required
        log.debug("[COR_AND_CSRF_AUTO_CONFIG] - Use default cors and csrf configuration: CSRF is disabled");
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
    }

    public void addFilters(HttpSecurity http) {
        if (Objects.nonNull(corsFilter)) {
            log.debug("[CORS_FILTER_AUTO_CONFIG] - Use filer: {}", corsFilter.getClass().getName());
            http.addFilterAfter(corsFilter, HeaderWriterFilter.class);
        }

        if (Objects.nonNull(authenticationFilter)) {
            log.debug("[AUTHENTICATION_FILTER_AUTO_CONFIG] - Use filer: {}", authenticationFilter.getClass().getName());
            http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    public void exceptionHandlers(HttpSecurity http) throws Exception {
        log.debug("[AUTHENTICATION_EXCEPTION_HANDLER_AUTO_CONFIG] - Use default exception handlers configuration");
        http.exceptionHandling(handler -> handler
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );
    }

    public void headersSecurity(HttpSecurity http) throws Exception {
        log.debug("[HEADER_SECURITY_AUTO_CONFIG] - Use default headers security configuration");
        http.headers(header -> header
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            .referrerPolicy(config ->
                config.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
        );
    }

    public void sessionManagementStrategy(HttpSecurity http) throws Exception {
        log.debug("[SESSION_MANAGEMENT_STRATEGY_AUTO_CONFIG] - Use default session management strategy: STATELESS");
        http.sessionManagement(sessionManager ->
                sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    public void authorizeHttpRequests(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        String[] publicApiPatterns = securityProps.getPublicRequestPatterns();
        log.debug("[AUTHORIZE_REQUEST_AUTO_CONFIG] - Use default configuration: FormLogin is disabled");
        log.debug("[AUTHORIZE_REQUEST_AUTO_CONFIG] - Ignore authorize requests: {}", Arrays.toString(publicApiPatterns));
        http.authorizeHttpRequests(registry -> registry
            .requestMatchers(SecurityUtils.convertToMvcMatchers(mvc, publicApiPatterns))
            .permitAll()
            // Used with custom CORS filters in CORS (Cross-Origin Resource Sharing) mechanism
            // The browser will send OPTIONS requests (preflight requests) to check
            // if the server allows access from other sources before send requests such as POST, GET
            .requestMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);
    }

    @SuppressWarnings("unused")
    public void oauth2(HttpSecurity http) throws Exception {
        // Add logic Oauth2 config here
    }
}
