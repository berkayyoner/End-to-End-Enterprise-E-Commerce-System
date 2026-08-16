package com.berkay.auth_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Everything that is not an Authorization Server endpoint (see {@link AuthorizationServerConfig},
 * ordered ahead of this chain): registration, and the form-login processing URL the SPA's own
 * login page (task 1.8) POSTs credentials to. Login responds with JSON, not a redirect, since
 * it's called via fetch - the SPA then separately drives the /oauth2/authorize + /oauth2/token
 * exchange (OAUTH2.md) using the session this establishes.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final ObjectMapper objectMapper;

	public SecurityConfig(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						// Registration has no prior session to carry a CSRF cookie yet; the
						// same protection isn't needed here since it doesn't ride on existing
						// authentication the way /login's session-establishing POST does.
						.ignoringRequestMatchers("/register"))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/register", "/csrf-token").permitAll()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginProcessingUrl("/login")
						.successHandler(jsonAuthenticationSuccessHandler())
						.failureHandler(jsonAuthenticationFailureHandler())
						.permitAll());

		return http.build();
	}

	private AuthenticationSuccessHandler jsonAuthenticationSuccessHandler() {
		return (request, response, authentication) -> writeJson(response, HttpServletResponse.SC_OK,
				Map.of("email", authentication.getName()));
	}

	private AuthenticationFailureHandler jsonAuthenticationFailureHandler() {
		return (request, response, exception) -> writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
				Map.of("error", "Invalid email or password"));
	}

	private void writeJson(HttpServletResponse response, int status, Map<String, Object> body) throws java.io.IOException {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("timestamp", Instant.now());
		payload.put("status", status);
		payload.putAll(body);

		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), payload);
	}
}
