package com.dct.base.config.properties;

import com.dct.base.constants.ActivateStatus;
import com.dct.base.constants.BasePropertiesConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Contains security configurations such as the secret key<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#SECURITY_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@ConfigurationProperties(prefix = BasePropertiesConstants.SECURITY_CONFIG)
public class SecurityProps {

    private boolean enabledTls;
    private ActivateStatus defaultAccessDeniedHandler;
    private ActivateStatus defaultAuthenticationEntrypointHandler;
    private Integer passwordEncryptFactor;
    private List<String> publicRequestPatterns;

    public boolean isEnabledTls() {
        return enabledTls;
    }

    public void setEnabledTls(boolean enabledTls) {
        this.enabledTls = enabledTls;
    }

    public ActivateStatus getDefaultAccessDeniedHandler() {
        return defaultAccessDeniedHandler;
    }

    public void setDefaultAccessDeniedHandler(ActivateStatus defaultAccessDeniedHandler) {
        this.defaultAccessDeniedHandler = defaultAccessDeniedHandler;
    }

    public ActivateStatus getDefaultAuthenticationEntrypointHandler() {
        return defaultAuthenticationEntrypointHandler;
    }

    public void setDefaultAuthenticationEntrypointHandler(ActivateStatus defaultAuthenticationEntrypointHandler) {
        this.defaultAuthenticationEntrypointHandler = defaultAuthenticationEntrypointHandler;
    }

    public Integer getPasswordEncryptFactor() {
        return passwordEncryptFactor;
    }

    public void setPasswordEncryptFactor(Integer passwordEncryptFactor) {
        this.passwordEncryptFactor = passwordEncryptFactor;
    }

    public List<String> getPublicRequestPatterns() {
        return publicRequestPatterns;
    }

    public void setPublicRequestPatterns(List<String> publicRequestPatterns) {
        this.publicRequestPatterns = publicRequestPatterns;
    }
}
