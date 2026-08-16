package com.berkay.auth_service.sellerapplication.controller;

import com.berkay.auth_service.sellerapplication.dto.SellerApplicationRequest;
import com.berkay.auth_service.sellerapplication.dto.SellerApplicationResponse;
import com.berkay.auth_service.sellerapplication.service.SellerApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public-account-facing submission (RULES.md: "Sell on Berkay" redirects here). Matched by the
 * default SecurityConfig chain, so the caller is an authenticated AppUser session.
 */
@RestController
public class SellerApplicationController {

	private final SellerApplicationService sellerApplicationService;

	public SellerApplicationController(SellerApplicationService sellerApplicationService) {
		this.sellerApplicationService = sellerApplicationService;
	}

	@PostMapping("/seller-applications")
	public ResponseEntity<SellerApplicationResponse> submit(Authentication authentication,
			@Valid @RequestBody SellerApplicationRequest request) {
		SellerApplicationResponse response = sellerApplicationService.submit(authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
