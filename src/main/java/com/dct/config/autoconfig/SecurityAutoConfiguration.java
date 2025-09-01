package com.dct.config.autoconfig;

import com.dct.config.security.handler.DefaultBaseAccessDeniedHandler;
import com.dct.config.security.handler.DefaultBaseAuthenticationEntryPoint;
import com.dct.config.exception.BaseExceptionHandler;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.config.properties.CorsProps;
import com.dct.model.config.properties.SecurityProps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Optional;

@AutoConfiguration
@EnableConfigurationProperties({SecurityProps.class, CorsProps.class})
public class SecurityAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
    private final SecurityProps securityProps;

    public SecurityAutoConfiguration(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler defaultAccessDeniedHandler(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[ACCESS_DENIED_AUTO_CONFIG] - Use default access denied handler");
        return new DefaultBaseAccessDeniedHandler(messageTranslationUtils);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint defaultAuthenticationEntryPoint(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[AUTHENTICATION_ENTRYPOINT_AUTO_CONFIG] - Use default authentication entry point");
        return new DefaultBaseAuthenticationEntryPoint(messageTranslationUtils);
    }

    @Bean
    @ConditionalOnMissingBean(MvcRequestMatcher.Builder.class)
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        log.debug("[MCV_REQUEST_MATCHER_AUTO_CONFIG] - Use default mvc request matcher builder");
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        int costFactor = Optional.ofNullable(securityProps).orElse(new SecurityProps()).getPasswordEncryptFactor();
        log.debug("[PASSWORD_ENCODER_AUTO_CONFIG] - Use default password encoder with encrypt factor: {}", costFactor);
        return new BCryptPasswordEncoder(costFactor);
    }

    /**
     * Configure a custom AuthenticationProvider to replace the default provider in Spring Security <p>
     * Method `setHideUserNotFoundExceptions` allows {@link UsernameNotFoundException} to be thrown
     * when an account is not found instead of convert to {@link BadCredentialsException} by default <p>
     * After that, the {@link UsernameNotFoundException} will be handle by {@link BaseExceptionHandler}
     */
    @Bean
    @ConditionalOnBean(UserDetailsService.class)
    @ConditionalOnMissingBean(AuthenticationProvider.class)
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        log.debug("[DAO_AUTHENTICATION_PROVIDER_AUTO_CONFIG] - Use default authentication provider");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * This is a bean that provides an AuthenticationManager from Spring Security, used to authenticate users
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        log.debug("[AUTHENTICATION_MANAGER_AUTO_CONFIG] - Use default authentication manager");
        return auth.getAuthenticationManager();
    }
}
