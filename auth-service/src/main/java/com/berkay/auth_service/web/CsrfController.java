package com.berkay.auth_service.web;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Security's {@code CookieCsrfTokenRepository} defers writing the XSRF-TOKEN cookie
 * until a {@link CsrfToken} is actually read from the request. The SPA (task 1.8) calls this
 * once on load so the cookie exists before it POSTs to /login.
 */
@RestController
public class CsrfController {

	@GetMapping("/csrf-token")
	public CsrfToken csrfToken(CsrfToken token) {
		return token;
	}
}
