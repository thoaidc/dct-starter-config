package com.dct.config.autoconfig;

import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BaseCommonConstants;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link EnableJpaAuditing} help JPA fills in information about the creator, the modifier in the entities
 *
 * @author thoaidc
 */
@AutoConfiguration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_AUDITING, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditingAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AuditingAutoConfiguration.class);

    /**
     * Helps JPA automatically handle annotations like @{@link CreatedBy}, @{@link LastModifiedBy},... in entities
     * @return {@link AuditorAware}
     */
    @Bean(name = "auditorProvider")
    @ConditionalOnMissingBean(AuditorAware.class)
    public AuditorAware<String> auditorProvider() {
        log.debug("[AUDITING_AUTO_CONFIG] - Default AuditorProvider initialized successful");

        return () -> {
            // Get the current username from the SecurityContext, using a default value if no user is authenticated
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = Objects.nonNull(context) ? context.getAuthentication() : null;
            String credential = Objects.nonNull(authentication) ? authentication.getName() : null;
            return Optional.of(Optional.ofNullable(credential).orElse(BaseCommonConstants.DEFAULT_CREATOR));
        };
    }

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }
}
