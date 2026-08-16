package com.berkay.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Auto-registered on any service that has common-lib on the classpath (via
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports),
 * so every service gets {@code @CreatedBy}/{@code @LastModifiedBy} population without
 * per-service wiring.
 */
@AutoConfiguration
@ConditionalOnClass(EnableJpaAuditing.class)
@EnableJpaAuditing(auditorAwareRef = "berkayAuditorProvider")
public class JpaAuditingAutoConfiguration {

	@Bean
	public AuditorAware<String> berkayAuditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null
					|| !authentication.isAuthenticated()
					|| "anonymousUser".equals(authentication.getPrincipal())) {
				return Optional.of("system");
			}
			return Optional.of(authentication.getName());
		};
	}
}
