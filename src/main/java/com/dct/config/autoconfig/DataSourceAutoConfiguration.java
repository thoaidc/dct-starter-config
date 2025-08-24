package com.dct.config.autoconfig;

import com.dct.model.config.properties.DataSourceProps;
import com.dct.model.config.properties.HikariDataSourceProps;
import com.dct.model.config.properties.HikariProps;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BaseExceptionConstants;
import com.dct.model.constants.BasePropertiesConstants;
import com.dct.model.exception.BaseIllegalArgumentException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_DATASOURCE, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableConfigurationProperties({
    DataSourceProps.class,
    HikariProps.class,
    HikariDataSourceProps.class
})
public class DataSourceAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataSourceAutoConfiguration.class);
    private static final String ENTITY_NAME = "com.dct.config.autoconfig.DataSourceAutoConfiguration";
    private final DataSourceProps dataSourceProps;
    private final HikariProps hikariProps;
    private final HikariDataSourceProps hikariDataSourceProps;

    public DataSourceAutoConfiguration(DataSourceProps dataSourceProps,
                                       HikariProps hikariProps,
                                       HikariDataSourceProps hikariDataSourceProps) {
        this.dataSourceProps = dataSourceProps;
        this.hikariProps = hikariProps;
        this.hikariDataSourceProps = hikariDataSourceProps;
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource defaultDataSource() {
        log.debug("[DATASOURCE_AUTO_CONFIG] - Use Hikari data source as default");
        HikariConfig hikariConfig = new HikariConfig();
        Properties properties = new Properties();

        if (Objects.isNull(dataSourceProps)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, BaseExceptionConstants.DATASOURCE_CONFIG_NOT_NULL);
        }

        hikariConfig.setDriverClassName(dataSourceProps.getDriverClassName());
        hikariConfig.setJdbcUrl(dataSourceProps.getUrl());
        hikariConfig.setUsername(dataSourceProps.getUsername());
        hikariConfig.setPassword(dataSourceProps.getPassword());

        if (Objects.nonNull(this.hikariProps)) {
            hikariConfig.setAutoCommit(hikariProps.getAutoCommit());
            hikariConfig.setAllowPoolSuspension(hikariProps.getAllowPoolSuspension());
            hikariConfig.setMaximumPoolSize(hikariProps.getMaximumPoolSize());
            hikariConfig.setMinimumIdle(hikariProps.getMinimumIdle());
            hikariConfig.setIdleTimeout(hikariProps.getIdleTimeout());
            hikariConfig.setMaxLifetime(hikariProps.getMaxLifetime());
            hikariConfig.setConnectionTimeout(hikariProps.getConnectionTimeout());
            hikariConfig.setPoolName(hikariProps.getPoolName());
        }

        // Health check configuration
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setValidationTimeout(5000);

        if (Objects.nonNull(hikariDataSourceProps)) {
            properties.setProperty("cachePrepStmts", String.valueOf(hikariDataSourceProps.getCachePrepStmts()));
            properties.setProperty("prepStmtCacheSize", String.valueOf(hikariDataSourceProps.getPrepStmtCacheSize()));
            properties.setProperty("prepStmtCacheSqlLimit", String.valueOf(hikariDataSourceProps.getPrepStmtCacheSqlLimit()));
            properties.setProperty("useServerPrepStmts", String.valueOf(hikariDataSourceProps.getUseServerPrepStmts()));
        }

        properties.setProperty("passwordCharacterEncoding", StandardCharsets.UTF_8.name());
        properties.setProperty("serverTimezone", "UTC"); // Uses the UTC standard for internationalized time
        hikariConfig.setDataSourceProperties(properties);

        log.info("[DATASOURCE_AUTO_CONFIG] - HikariCP DataSource configured successfully - Pool: {}, Max: {}, Min: {}",
                hikariProps.getPoolName(),
                hikariProps.getMaximumPoolSize(),
                hikariProps.getMinimumIdle());

        return new HikariDataSource(hikariConfig);
    }
}
