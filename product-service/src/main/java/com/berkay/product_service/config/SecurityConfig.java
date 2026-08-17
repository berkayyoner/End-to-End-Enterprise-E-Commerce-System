package com.berkay.product_service.config;

import com.berkay.common.security.PermissionAuthoritiesConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures product-service as an OAuth2 Resource Server, validating JWTs issued by auth-service.
 * Enables method-level security for @PreAuthorize decorators on category endpoints.
 * Categories are read-only publicly (GET/list permitted to all) but write operations
 * (POST/PUT/DELETE) require decomposed permissions (e.g. PERM_P4_ADD) verified via JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new PermissionAuthoritiesConverter());
		return converter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize
						// All GET requests on categories and products are public
						.requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/products/**").permitAll()
						// All other requests require authentication (permission validation via @PreAuthorize)
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

		return http.build();
	}
}
