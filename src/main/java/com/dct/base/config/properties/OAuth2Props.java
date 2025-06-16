package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Contains configuration properties related to Google OAuth2 in the application<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 * {@link BasePropertiesConstants#OAUTH2_CONFIG} decides the prefix for the configurations that will be mapped<p>
 *
 * {@link ConditionalOnProperty} mark this class to be initialized only if the property OAUTH2_ACTIVE_STATUS is "true"<p>
 *
 * See <a href="">application-test.yml</a> for detail
 *
 * @author thoaidc
 */
@Configuration
@ConfigurationProperties(prefix = BasePropertiesConstants.OAUTH2_CONFIG)
public class OAuth2Props {

    private boolean enabled;
    private String baseAuthorizeUri;
    private List<ClientProps> clients;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseAuthorizeUri() {
        return baseAuthorizeUri;
    }

    public void setBaseAuthorizeUri(String baseAuthorizeUri) {
        this.baseAuthorizeUri = baseAuthorizeUri;
    }

    public List<ClientProps> getClients() {
        return clients;
    }

    public void setClients(List<ClientProps> clients) {
        this.clients = clients;
    }

    public static class ClientProps {
        private String provider;
        private String clientId;
        private String clientName;
        private String clientSecret;
        private String authorizationUri;
        private String redirectUri;
        private String tokenUri;
        private String userInfoUri;
        private String usernameAttributeName;
        private List<String> scope;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getAuthorizationUri() {
            return authorizationUri;
        }

        public void setAuthorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public String getTokenUri() {
            return tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getUserInfoUri() {
            return userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }

        public String getUsernameAttributeName() {
            return usernameAttributeName;
        }

        public void setUsernameAttributeName(String usernameAttributeName) {
            this.usernameAttributeName = usernameAttributeName;
        }

        public List<String> getScope() {
            return scope;
        }

        public void setScope(List<String> scope) {
            this.scope = scope;
        }
    }
}
