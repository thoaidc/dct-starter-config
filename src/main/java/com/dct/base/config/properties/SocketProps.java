package com.dct.base.config.properties;

import com.dct.base.constants.BaseCommonConstants;
import com.dct.base.constants.BasePropertiesConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

/**
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
@ConfigurationProperties(prefix = BasePropertiesConstants.SOCKET_CONFIG)
public class SocketProps {
    private String[] brokerPrefixes;
    private String[] applicationPrefixes;
    private String[] endpoints;

    public String[] getBrokerPrefixes() {
        return Optional.ofNullable(brokerPrefixes).orElse(BaseCommonConstants.Socket.DEFAULT_BROKER_PREFIXES);
    }

    public void setBrokerPrefixes(String[] brokerPrefixes) {
        this.brokerPrefixes = brokerPrefixes;
    }

    public String[] getApplicationPrefixes() {
        return Optional.ofNullable(applicationPrefixes).orElse(BaseCommonConstants.Socket.DEFAULT_APPLICATION_PREFIXES);
    }

    public void setApplicationPrefixes(String[] applicationPrefixes) {
        this.applicationPrefixes = applicationPrefixes;
    }

    public String[] getEndpoints() {
        return Optional.ofNullable(endpoints).orElse(BaseCommonConstants.Socket.DEFAULT_ENDPOINTS);
    }

    public void setEndpoints(String[] endpoints) {
        this.endpoints = endpoints;
    }
}
