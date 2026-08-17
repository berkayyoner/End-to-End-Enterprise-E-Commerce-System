package com.berkay.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Enables method-level security for @PreAuthorize decorators on category endpoints.
 * Categories are read-only publicly (GET/list permitted to all) but write operations
 * (POST/PUT/DELETE) require P4_* permissions verified via JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize
						// All GET requests on categories are public
						.requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
						// All other requests require authentication (permission validation via @PreAuthorize)
						.anyRequest().authenticated());

		return http.build();
	}
}
