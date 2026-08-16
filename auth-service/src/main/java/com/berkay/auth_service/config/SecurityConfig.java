package com.berkay.auth_service.config;

import com.berkay.auth_service.personnel.security.PersonnelDetailsService;
import com.berkay.auth_service.user.security.AppUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
 * Two separate login surfaces, each its own {@link SecurityFilterChain} with its own explicit
 * {@link DaoAuthenticationProvider} (rather than relying on Spring Boot's single
 * auto-wired global UserDetailsService, which can't disambiguate between two candidates):
 * public account login at /login (AppUserDetailsService) and personnel login at
 * /personnel/login (PersonnelDetailsService) - RULES.md requires these stay separate. Both
 * respond with JSON, not a redirect, since the SPA calls them via fetch; the SPA then drives the
 * /oauth2/authorize + /oauth2/token exchange (OAUTH2.md) using the session either establishes.
 * The Authorization Server's own endpoints are handled by {@link AuthorizationServerConfig},
 * ordered ahead of both of these.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AppUserDetailsService appUserDetailsService,
			PasswordEncoder passwordEncoder) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(appUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder);

		http
				.securityMatcher("/register", "/login", "/csrf-token")
				.authenticationProvider(provider)
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

	@Bean
	@Order(3)
	public SecurityFilterChain personnelSecurityFilterChain(HttpSecurity http,
			PersonnelDetailsService personnelDetailsService, PasswordEncoder passwordEncoder) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(personnelDetailsService);
		provider.setPasswordEncoder(passwordEncoder);

		http
				.securityMatcher("/personnel/**")
				.authenticationProvider(provider)
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.formLogin(form -> form
						.loginProcessingUrl("/personnel/login")
						.successHandler(jsonAuthenticationSuccessHandler())
						.failureHandler(jsonAuthenticationFailureHandler())
						.permitAll());

		return http.build();
	}

	@Bean
	@Order(Integer.MAX_VALUE)
	public SecurityFilterChain fallbackSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
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
