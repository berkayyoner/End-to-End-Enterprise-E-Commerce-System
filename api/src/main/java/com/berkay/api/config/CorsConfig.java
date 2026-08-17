package com.berkay.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * RULES.md: the public storefront is open to anonymous visitors from anywhere, while the
 * personnel/admin panel is a separate front-end that must only be reachable from localhost or
 * explicitly allow-listed IPs. Personnel routes live under /api/personnel/** so this one gateway
 * can enforce both policies without the downstream services knowing about CORS at all.
 */
@Configuration
@EnableConfigurationProperties(GatewayCorsProperties.class)
public class CorsConfig implements WebMvcConfigurer {

	private final GatewayCorsProperties corsProperties;

	public CorsConfig(GatewayCorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// Personnel endpoints across all services (auth-service, product-service, etc.)
		// Pattern: /api/*/personnel/** catches both /api/auth/personnel/** and /api/products/personnel/**
		registry.addMapping("/api/*/personnel/**")
				.allowedOrigins(corsProperties.getPersonnelAllowedOrigins().toArray(String[]::new))
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);

		registry.addMapping("/**")
				.allowedOriginPatterns(corsProperties.getPublicAllowedOrigins().toArray(String[]::new))
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
