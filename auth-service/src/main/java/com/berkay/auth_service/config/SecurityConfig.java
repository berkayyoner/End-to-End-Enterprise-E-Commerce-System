package com.berkay.auth_service.config;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.activitylog.ActorType;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
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
 * ordered ahead of both of these. Login/logout (success and failure) are reported to log-service
 * per task 1.7's activity logging hooks.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final ObjectMapper objectMapper;
	private final ActivityLogClient activityLogClient;

	public SecurityConfig(ObjectMapper objectMapper, ActivityLogClient activityLogClient) {
		this.objectMapper = objectMapper;
		this.activityLogClient = activityLogClient;
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
				.securityMatcher("/register", "/login", "/logout", "/csrf-token", "/me", "/id-verifications",
						"/id-verifications/me", "/seller-applications", "/seller-applications/me", "/sellers/me",
						"/sellers/me/store-name")
				.authenticationProvider(provider)
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						// Registration has no prior session to carry a CSRF cookie yet; the
						// same protection isn't needed here since it doesn't ride on existing
						// authentication the way /login's session-establishing POST does.
						.ignoringRequestMatchers("/register"))
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jsonAuthenticationEntryPoint()))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/register", "/csrf-token", "/sellers/*/public-profile").permitAll()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginProcessingUrl("/login")
						.successHandler(jsonAuthenticationSuccessHandler(ActorType.USER, "USER_LOGIN_SUCCESS"))
						.failureHandler(jsonAuthenticationFailureHandler(ActorType.USER, "USER_LOGIN_FAILURE"))
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessHandler(jsonLogoutSuccessHandler(ActorType.USER, "USER_LOGOUT"))
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
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jsonAuthenticationEntryPoint()))
				.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.formLogin(form -> form
						.loginProcessingUrl("/personnel/login")
						.successHandler(jsonAuthenticationSuccessHandler(ActorType.PERSONNEL, "PERSONNEL_LOGIN_SUCCESS"))
						.failureHandler(jsonAuthenticationFailureHandler(ActorType.PERSONNEL, "PERSONNEL_LOGIN_FAILURE"))
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/personnel/logout")
						.logoutSuccessHandler(jsonLogoutSuccessHandler(ActorType.PERSONNEL, "PERSONNEL_LOGOUT"))
						.permitAll());

		return http.build();
	}

	@Bean
	@Order(Integer.MAX_VALUE)
	public SecurityFilterChain fallbackSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
		return http.build();
	}

	/**
	 * Without this, an unauthenticated request to a protected endpoint (e.g. GET /me) hits
	 * formLogin's default {@code AuthenticationEntryPoint}, which redirects (302) to an HTML
	 * login page - fine for a server-rendered app, but the SPA (berkay-public, task 1.8) calls
	 * these as plain JSON fetches and needs a 401 body, not a redirect, to detect "not logged in".
	 */
	private AuthenticationEntryPoint jsonAuthenticationEntryPoint() {
		return (request, response, authException) ->
				writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, Map.of("error", "Authentication required"));
	}

	private AuthenticationSuccessHandler jsonAuthenticationSuccessHandler(ActorType actorType, String action) {
		return (request, response, authentication) -> {
			activityLogClient.log(actorType, null, action, "email=" + authentication.getName());
			writeJson(response, HttpServletResponse.SC_OK, Map.of("email", authentication.getName()));
		};
	}

	private AuthenticationFailureHandler jsonAuthenticationFailureHandler(ActorType actorType, String action) {
		return (request, response, exception) -> {
			String attemptedEmail = request.getParameter("username");
			activityLogClient.log(actorType, null, action, "email=" + attemptedEmail);
			writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, Map.of("error", "Invalid email or password"));
		};
	}

	private LogoutSuccessHandler jsonLogoutSuccessHandler(ActorType actorType, String action) {
		return (request, response, authentication) -> {
			if (authentication != null) {
				activityLogClient.log(actorType, null, action, "email=" + authentication.getName());
			}
			writeJson(response, HttpServletResponse.SC_OK, Map.of("message", "Logged out"));
		};
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
