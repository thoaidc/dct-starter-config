package com.dct.config.autoconfig;

import com.dct.config.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.config.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.config.security.handler.DefaultBaseOAuth2AuthenticationFailureHandler;
import com.dct.config.security.handler.DefaultBaseOAuth2AuthenticationSuccessHandler;
import com.dct.config.security.handler.DefaultOAuth2AuthRequestResolver;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.config.properties.SecurityProps;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BasePropertiesConstants;
import com.dct.model.exception.BaseIllegalArgumentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;
import java.util.Objects;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_OAUTH2, havingValue = ActivateStatus.ENABLED_VALUE)
public class OAuth2AutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AutoConfiguration.class);
    private static final String ENTITY_NAME = "com.dct.config.autoconfig.OAuth2AutoConfiguration";
    private final SecurityProps.OAuth2Config oAuth2Config;

    public OAuth2AutoConfiguration(SecurityProps securityProps) {
        this.oAuth2Config = securityProps.getOauth2();

        if (Objects.isNull(oAuth2Config)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "OAuth2Config must not be null when enabled");
        }
    }

    /**
     * Register {@link DefaultOAuth2AuthorizationRequestResolver} to adjust the parameters of the OAuth2 request
     * before sending it to the provider (such as Google)
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationRequestResolver.class)
    public OAuth2AuthorizationRequestResolver defaultOAuth2AuthRequestResolver(ClientRegistrationRepository registry) {
        log.debug("[OAUTH2_REQUEST_RESOLVER_AUTO_CONFIG] - Using bean OAuth2AuthorizationRequestResolver as default");
        return new DefaultOAuth2AuthRequestResolver(registry, oAuth2Config);
    }

    @Bean
    @ConditionalOnMissingBean(BaseOAuth2AuthenticationSuccessHandler.class)
    public BaseOAuth2AuthenticationSuccessHandler defaultOAuth2SuccessHandler() {
        log.debug("[OAUTH2_SUCCESS_HANDLER_AUTO_CONFIG] - Using bean OAuth2AuthenticationSuccessHandler as default");
        return new DefaultBaseOAuth2AuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(BaseOAuth2AuthenticationFailureHandler.class)
    public BaseOAuth2AuthenticationFailureHandler defaultOAuth2FailureHandler(MessageTranslationUtils messageUtils) {
        log.debug("[OAUTH2_FAILURE_HANDLER_AUTO_CONFIG] - Using bean OAuth2AuthenticationFailureHandler as default");
        return new DefaultBaseOAuth2AuthenticationFailureHandler(messageUtils);
    }

    /**
     * Returns a {@link ClientRegistrationRepository} to manage the configuration details of OAuth2 providers <p>
     * {@link InMemoryClientRegistrationRepository} is used to store the client registration information in memory
     */
    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public ClientRegistrationRepository defaultClientRegistrationRepository() {
        log.debug("[OAUTH2_CLIENT_REGISTRATION_AUTO_CONFIG] - Use InMemoryClientRegistrationRepository as default");
        return new InMemoryClientRegistrationRepository(clientRegistrations());
    }

    private ClientRegistration[] clientRegistrations() {
        List<SecurityProps.OAuth2Config.ClientProps> clientProps = oAuth2Config.getClients();

        if (Objects.isNull(clientProps) || clientProps.isEmpty()) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Not found client to register OAuth2 config");
        }

        return clientProps.stream()
                .map(clientProp ->
                    ClientRegistration.withRegistrationId(clientProp.getProvider())
                    .clientId(clientProp.getClientId())
                    .clientName(clientProp.getClientName())
                    .clientSecret(clientProp.getClientSecret())
                    .scope(clientProp.getScope())
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationUri(clientProp.getAuthorizationUri())
                    .redirectUri(clientProp.getRedirectUri())
                    .tokenUri(clientProp.getTokenUri())
                    .userInfoUri(clientProp.getUserInfoUri())
                    .userNameAttributeName(clientProp.getUsernameAttributeName())
                    .build()
                ).toArray(ClientRegistration[]::new);
    }
}
