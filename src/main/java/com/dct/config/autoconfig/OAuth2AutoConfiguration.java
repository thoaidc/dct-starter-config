package com.dct.config.autoconfig;

import com.dct.config.security.handler.BaseOAuth2AuthenticationFailureHandler;
import com.dct.config.security.handler.BaseOAuth2AuthenticationSuccessHandler;
import com.dct.config.security.handler.DefaultBaseOAuth2AuthenticationFailureHandler;
import com.dct.config.security.handler.DefaultBaseOAuth2AuthenticationSuccessHandler;
import com.dct.config.security.handler.DefaultOAuth2AuthRequestResolver;
import com.dct.model.common.MessageTranslationUtils;
import com.dct.model.config.properties.OAuth2Props;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BasePropertiesConstants;
import com.dct.model.exception.BaseIllegalArgumentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;
import java.util.Objects;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_OAUTH2, havingValue = "true")
@EnableConfigurationProperties(OAuth2Props.class)
public class OAuth2AutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AutoConfiguration.class);
    private static final String ENTITY_NAME = "OAuth2AutoConfiguration";
    private final OAuth2Props oAuth2Props;

    public OAuth2AutoConfiguration(OAuth2Props oAuth2Props) {
        this.oAuth2Props = oAuth2Props;
    }

    /**
     * Register {@link DefaultOAuth2AuthorizationRequestResolver} to adjust the parameters of the OAuth2 request
     * before sending it to the provider (such as Google)
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationRequestResolver.class)
    public OAuth2AuthorizationRequestResolver defaultOAuth2AuthRequestResolver(ClientRegistrationRepository registry) {
        log.debug("[{}] - Auto configure default OAuth2AuthorizationRequestResolver", ENTITY_NAME);
        return new DefaultOAuth2AuthRequestResolver(registry, oAuth2Props);
    }

    @Bean
    @ConditionalOnMissingBean(BaseOAuth2AuthenticationSuccessHandler.class)
    public BaseOAuth2AuthenticationSuccessHandler defaultOAuth2SuccessHandler() {
        log.debug("[{}] - Auto configure default OAuth2AuthenticationSuccessHandler", ENTITY_NAME);
        return new DefaultBaseOAuth2AuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(BaseOAuth2AuthenticationFailureHandler.class)
    public BaseOAuth2AuthenticationFailureHandler defaultOAuth2FailureHandler(
        @Autowired(required = false) MessageTranslationUtils messageTranslationUtils,
        Environment env
    ) {
        log.debug("[{}] - Auto configure default OAuth2AuthenticationFailureHandler", ENTITY_NAME);
        String isI18nEnabled = env.getProperty(BasePropertiesConstants.ENABLED_I18N);

        if (ActivateStatus.ENABLED_VALUE.equals(isI18nEnabled) && Objects.nonNull(messageTranslationUtils)) {
            return new DefaultBaseOAuth2AuthenticationFailureHandler(messageTranslationUtils);
        }

        return new DefaultBaseOAuth2AuthenticationFailureHandler();
    }

    /**
     * Returns a {@link ClientRegistrationRepository} to manage the configuration details of OAuth2 providers <p>
     * {@link InMemoryClientRegistrationRepository} is used to store the client registration information in memory
     */
    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public ClientRegistrationRepository defaultClientRegistrationRepository() {
        log.debug("[{}] - Registered OAuth2 clients successfully", ENTITY_NAME);
        return new InMemoryClientRegistrationRepository(clientRegistrations());
    }

    private ClientRegistration[] clientRegistrations() {
        List<OAuth2Props.ClientProps> clientProps = oAuth2Props.getClients();

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
