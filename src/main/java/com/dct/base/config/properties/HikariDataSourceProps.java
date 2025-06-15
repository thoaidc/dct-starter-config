package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Contains configuration properties related to optimizing database connections<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#HIKARI_DATASOURCE_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@Configuration
@ConfigurationProperties(prefix = BasePropertiesConstants.HIKARI_DATASOURCE_CONFIG)
public class HikariDataSourceProps {

    private boolean cachePrepStmts;
    private boolean useServerPrepStmts;
    private int prepStmtCacheSize;
    private int prepStmtCacheSqlLimit;

    public boolean getCachePrepStmts() {
        return cachePrepStmts;
    }

    public void setCachePrepStmts(boolean cachePrepStmts) {
        this.cachePrepStmts = cachePrepStmts;
    }

    public boolean getUseServerPrepStmts() {
        return useServerPrepStmts;
    }

    public void setUseServerPrepStmts(boolean useServerPrepStmts) {
        this.useServerPrepStmts = useServerPrepStmts;
    }

    public int getPrepStmtCacheSize() {
        return prepStmtCacheSize > 0 ? prepStmtCacheSize : 100; // Default cache 100 sql query
    }

    public void setPrepStmtCacheSize(int prepStmtCacheSize) {
        this.prepStmtCacheSize = prepStmtCacheSize;
    }

    public int getPrepStmtCacheSqlLimit() {
        // By default, only SQL queries with length less than 2048 characters are cached
        return prepStmtCacheSqlLimit > 0 ? prepStmtCacheSqlLimit : 2048;
    }

    public void setPrepStmtCacheSqlLimit(int prepStmtCacheSqlLimit) {
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    }
}
