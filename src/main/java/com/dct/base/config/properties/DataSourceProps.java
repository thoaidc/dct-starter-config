package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.exception.BaseIllegalArgumentException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Contains the configuration for the datasource, such as the database connection url, username, password, etc<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#DATASOURCE_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@Configuration
@ConfigurationProperties(prefix = BasePropertiesConstants.DATASOURCE_CONFIG)
public class DataSourceProps {

    private static final String ENTITY_NAME = "DataSourceProps";
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public String getDriverClassName() {
        if (StringUtils.hasText(driverClassName))
            return driverClassName;

        throw new BaseIllegalArgumentException(ENTITY_NAME, "DriverClassName is required");
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        if (StringUtils.hasText(url))
            return url;

        throw new BaseIllegalArgumentException(ENTITY_NAME, "Database URL is required");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        if (StringUtils.hasText(username))
            return username;

        throw new BaseIllegalArgumentException(ENTITY_NAME, "Database username is required");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (StringUtils.hasText(password))
            return password;

        throw new BaseIllegalArgumentException(ENTITY_NAME, "Database password is required");
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
