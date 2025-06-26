package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#SECURITY_JWT_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@ConfigurationProperties(prefix = BasePropertiesConstants.SECURITY_JWT_CONFIG)
public class JwtProps {
    private boolean enabled;
    private String base64SecretKey;
    private AccessTokenConfig accessToken;
    private RefreshTokenConfig refreshToken;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBase64SecretKey() {
        return base64SecretKey;
    }

    public void setBase64SecretKey(String base64SecretKey) {
        this.base64SecretKey = base64SecretKey;
    }

    public AccessTokenConfig getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessTokenConfig accessToken) {
        this.accessToken = accessToken;
    }

    public RefreshTokenConfig getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshTokenConfig refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class AccessTokenConfig {
        private Long validity;

        public Long getValidity() {
            return validity;
        }

        public void setValidity(Long validity) {
            this.validity = validity;
        }
    }

    public static class RefreshTokenConfig {
        private Long validity;
        private Long validityForRemember;

        public Long getValidity() {
            return validity;
        }

        public void setValidity(Long validity) {
            this.validity = validity;
        }

        public Long getValidityForRemember() {
            return validityForRemember;
        }

        public void setValidityForRemember(Long validityForRemember) {
            this.validityForRemember = validityForRemember;
        }
    }
}
