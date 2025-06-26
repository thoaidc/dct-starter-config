package com.dct.base.autoconfig;

import com.dct.base.common.MessageTranslationUtils;
import com.dct.base.config.properties.SecurityProps;
import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.security.config.BaseSecurityAuthorizeRequestConfig;
import com.dct.base.security.config.DefaultBaseSecurityAuthorizeRequestConfig;
import com.dct.base.security.handler.DefaultBaseAccessDeniedHandler;
import com.dct.base.security.handler.DefaultBaseAuthenticationEntryPoint;
import com.dct.base.exception.handler.BaseExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
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

import java.util.Objects;
import java.util.Optional;

import static com.dct.base.constants.ActivateStatus.ENABLED_VALUE;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProps.class)
public class SecurityAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
    private static final String ENTITY_NAME = "SecurityAutoConfiguration";
    private final SecurityProps securityProps;

    public SecurityAutoConfiguration(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler defaultAccessDeniedHandler(
        @Autowired(required = false) MessageTranslationUtils messageTranslationUtils,
        Environment env
    ) {
        log.debug("[{}] - Auto configure default access denied handler", ENTITY_NAME);
        String isI18nEnabled = env.getProperty(BasePropertiesConstants.ENABLED_I18N);

        if (ENABLED_VALUE.equals(isI18nEnabled) && Objects.nonNull(messageTranslationUtils)) {
            return new DefaultBaseAccessDeniedHandler(messageTranslationUtils);
        }

        return new DefaultBaseAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint defaultAuthenticationEntryPoint(
        @Autowired(required = false) MessageTranslationUtils messageTranslationUtils,
        Environment env
    ) {
        log.debug("[{}] - Auto configure default authentication entry point", ENTITY_NAME);
        String isI18nEnabled = env.getProperty(BasePropertiesConstants.ENABLED_I18N);

        if (ENABLED_VALUE.equals(isI18nEnabled) && Objects.nonNull(messageTranslationUtils)) {
            return new DefaultBaseAuthenticationEntryPoint(messageTranslationUtils);
        }

        return new DefaultBaseAuthenticationEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean(MvcRequestMatcher.Builder.class)
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        log.debug("[{}] - Auto configure default mvc request matcher builder", ENTITY_NAME);
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        log.debug("[{}] - Auto configure default password encoder", ENTITY_NAME);
        int costFactor = Optional.ofNullable(securityProps).orElse(new SecurityProps()).getPasswordEncryptFactor();
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
    @ConditionalOnMissingBean(DaoAuthenticationProvider.class)
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        log.debug("[{}] - Auto configure default authentication provider", ENTITY_NAME);
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
        log.debug("[{}] - Auto configure default authentication manager", ENTITY_NAME);
        return auth.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean(BaseSecurityAuthorizeRequestConfig.class)
    public BaseSecurityAuthorizeRequestConfig defaultBaseSecurityAuthorizeRequestConfig() {
        log.debug("[{}] - Auto configure default security authorize request matchers", ENTITY_NAME);
        return new DefaultBaseSecurityAuthorizeRequestConfig(securityProps);
    }
}
