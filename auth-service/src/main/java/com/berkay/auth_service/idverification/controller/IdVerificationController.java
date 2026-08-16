package com.berkay.auth_service.idverification.controller;

import com.berkay.auth_service.idverification.dto.IdVerificationResponse;
import com.berkay.auth_service.idverification.service.IdVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Public-account-facing submission (RULES.md: buy/add-to-basket/sell all redirect here until
 * verified). Matched by the default SecurityConfig chain, so the caller is an authenticated
 * AppUser session, never personnel.
 */
@RestController
public class IdVerificationController {

	private final IdVerificationService idVerificationService;

	public IdVerificationController(IdVerificationService idVerificationService) {
		this.idVerificationService = idVerificationService;
	}

	@PostMapping(value = "/id-verifications", consumes = "multipart/form-data")
	public ResponseEntity<IdVerificationResponse> submit(
			Authentication authentication,
			@RequestParam String idNumber,
			@RequestParam MultipartFile frontPhoto,
			@RequestParam MultipartFile backPhoto) {
		IdVerificationResponse response = idVerificationService.submit(authentication.getName(), idNumber, frontPhoto, backPhoto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/id-verifications/me")
	public ResponseEntity<IdVerificationResponse> me(Authentication authentication) {
		return idVerificationService.findLatestForAppUser(authentication.getName())
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}
}
