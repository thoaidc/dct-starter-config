package com.dct.config.security.handler;

import com.dct.model.config.properties.SecurityProps;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Objects;

public class DefaultOAuth2AuthRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final Logger log = LoggerFactory.getLogger(DefaultOAuth2AuthRequestResolver.class);
    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    public DefaultOAuth2AuthRequestResolver(ClientRegistrationRepository client, SecurityProps.OAuth2Config oAuth2Config) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(client, oAuth2Config.getBaseAuthorizeUri());
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        log.debug("[OAUTH2_REQUEST_RESOLVER] - Custom resolver: Modifying oauth2 authorization request");

        if (Objects.isNull(authorizationRequest))
            return null;

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(params -> params.put("access_type", "offline"))
                .build();
    }
}
