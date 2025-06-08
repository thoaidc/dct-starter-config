package com.dct.base.security.config;

import com.dct.base.common.SecurityUtils;
import com.dct.base.constants.BaseSecurityConstants;
import com.dct.base.security.jwt.BaseJwtFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;

@SuppressWarnings("unused")
public abstract class BaseSecurityFilterChain {

    private static final Logger log = LoggerFactory.getLogger(BaseSecurityFilterChain.class);
    private static final String ENTITY_NAME = "BaseSecurityFilterChain";
    private final CorsFilter corsFilter;
    private final BaseJwtFilter jwtFilter;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig;

    protected BaseSecurityFilterChain(CorsFilter corsFilter,
                                      BaseJwtFilter jwtFilter,
                                      AccessDeniedHandler accessDeniedHandler,
                                      AuthenticationEntryPoint authenticationEntryPoint,
                                      BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig) {
        this.corsFilter = corsFilter;
        this.jwtFilter = jwtFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
    }

    protected SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        cors(http);
        addFilters(http);
        exceptionHandlers(http);
        headersSecurity(http);
        sessionManagementStrategy(http);
        authorizeHttpRequests(http, mvc);
        oauth2(http);
        return http.build();
    }

    protected void cors(HttpSecurity http) throws Exception {
        // Because of using JWT, CSRF is not required
        log.debug("[{}] - Use default cors and csrf configuration: CSRF is disabled", ENTITY_NAME);
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
    }

    protected void addFilters(HttpSecurity http) {
        log.debug("[{}] - Use default filters orders configuration", ENTITY_NAME);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(corsFilter, BaseJwtFilter.class);
    }

    protected void exceptionHandlers(HttpSecurity http) throws Exception {
        log.debug("[{}] - Use default exception handlers configuration", ENTITY_NAME);
        http.exceptionHandling(handler -> handler
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );
    }

    protected void headersSecurity(HttpSecurity http) throws Exception {
        log.debug("[{}] - Use default headers security configuration", ENTITY_NAME);
        http.headers(header -> header
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            .contentSecurityPolicy(policy -> policy.policyDirectives(BaseSecurityConstants.HEADER.SECURITY_POLICY))
            .referrerPolicy(config -> config.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .permissionsPolicy(config -> config.policy(BaseSecurityConstants.HEADER.PERMISSIONS_POLICY))
        );
    }

    protected void sessionManagementStrategy(HttpSecurity http) throws Exception {
        log.debug("[{}] - Use default session management strategy configuration: STATELESS", ENTITY_NAME);
        http.sessionManagement(sessionManager ->
                sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    protected void authorizeHttpRequests(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        log.debug("[{}] - Use default authorize http requests configuration: FormLogin is disabled", ENTITY_NAME);
        http.authorizeHttpRequests(registry -> registry
            .requestMatchers(SecurityUtils.convertToMvcMatchers(mvc, securityAuthorizeRequestConfig.getPublicPatterns()))
            .permitAll()
            // Used with custom CORS filters in CORS (Cross-Origin Resource Sharing) mechanism
            // The browser will send OPTIONS requests (preflight requests) to check
            // if the server allows access from other sources before send requests such as POST, GET
            .requestMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(AbstractHttpConfigurer::disable);
    }

    protected void oauth2(HttpSecurity http) {
        log.debug("[{}] - Use default oauth2 configuration", ENTITY_NAME);
    }
}
