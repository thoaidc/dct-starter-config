package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Contains configuration properties related to managing the connection pool of Hikari<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#HIKARI_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@ConfigurationProperties(prefix = BasePropertiesConstants.HIKARI_CONFIG)
public class HikariProps {

    private String poolName;
    private boolean autoCommit;
    private boolean allowPoolSuspension;
    private int maximumPoolSize;
    private int minimumIdle;
    private long idleTimeout;
    private long maxLifetime;
    private long connectionTimeout;
    private long leakDetectionThreshold;

    public String getPoolName() {
        return StringUtils.hasText(poolName) ? poolName : this.getClass().getName();
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public boolean getAllowPoolSuspension() {
        return allowPoolSuspension;
    }

    public void setAllowPoolSuspension(boolean allowPoolSuspension) {
        this.allowPoolSuspension = allowPoolSuspension;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize > 0 ? maximumPoolSize : 30;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getMinimumIdle() {
        return minimumIdle > 0 ? minimumIdle : 3;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public long getIdleTimeout() {
        return idleTimeout > 0 ? idleTimeout : 180000; // Default 3 minutes
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getMaxLifetime() {
        return maxLifetime > 0 ? maxLifetime : 1800000; // Default 30 minutes
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public long getConnectionTimeout() {
        return connectionTimeout > 0 ? connectionTimeout : 200000; // Default 3 minutes 20s
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getLeakDetectionThreshold() {
        return leakDetectionThreshold > 0 ? leakDetectionThreshold : 200000; // Default 3 minutes 20s
    }

    public void setLeakDetectionThreshold(long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }
}
