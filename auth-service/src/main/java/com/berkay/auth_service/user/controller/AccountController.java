package com.berkay.auth_service.user.controller;

import com.berkay.auth_service.user.dto.RegisterResponse;
import com.berkay.auth_service.user.service.RegistrationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Backs the SPA's own profile section (task 1.8): the current AppUser's own profile, read from
 * the session established by SecurityConfig's default chain. Matched by the same chain as
 * /register and /login, so the caller is always an authenticated AppUser.
 */
@RestController
public class AccountController {

	private final RegistrationService registrationService;

	public AccountController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@GetMapping("/me")
	public RegisterResponse me(Authentication authentication) {
		return registrationService.findByEmail(authentication.getName());
	}
}
