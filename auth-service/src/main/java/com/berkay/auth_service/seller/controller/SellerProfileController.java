package com.berkay.auth_service.seller.controller;

import com.berkay.auth_service.seller.dto.SellerProfileResponse;
import com.berkay.auth_service.seller.dto.SellerPublicProfileResponse;
import com.berkay.auth_service.seller.dto.UpdateStoreNameRequest;
import com.berkay.auth_service.seller.service.SellerProfileService;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 4.1 endpoints for seller profile management:
 * - GET /sellers/me - authenticated seller only
 * - PUT /sellers/me/store-name - authenticated seller only
 * - GET /sellers/{id}/public-profile - public access
 *
 * Seller-only endpoints are protected by manual role checking in the handler
 * (since this is session-based auth, not JWT resource-server).
 */
@RestController
public class SellerProfileController {

	private final SellerProfileService sellerProfileService;
	private final AppUserRepository appUserRepository;

	public SellerProfileController(SellerProfileService sellerProfileService,
			AppUserRepository appUserRepository) {
		this.sellerProfileService = sellerProfileService;
		this.appUserRepository = appUserRepository;
	}

	/**
	 * Get the authenticated seller's profile (store name, earnings, follower count).
	 * Requires: authenticated user with seller=true.
	 */
	@GetMapping("/sellers/me")
	public ResponseEntity<SellerProfileResponse> getMySellerProfile(Authentication authentication) {
		AppUser user = getUserFromAuthentication(authentication);
		if (!user.isSeller()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.ok(sellerProfileService.getSellerProfile(user.getId()));
	}

	/**
	 * Update the authenticated seller's store name.
	 * Requires: authenticated user with seller=true.
	 */
	@PutMapping("/sellers/me/store-name")
	public ResponseEntity<SellerProfileResponse> updateMyStoreName(Authentication authentication,
			@Valid @RequestBody UpdateStoreNameRequest request) {
		AppUser user = getUserFromAuthentication(authentication);
		if (!user.isSeller()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.ok(sellerProfileService.updateStoreName(user.getId(), request));
	}

	/**
	 * Get a seller's public profile (store name and follower count only).
	 * Public endpoint - no authentication required.
	 */
	@GetMapping("/sellers/{id}/public-profile")
	public ResponseEntity<SellerPublicProfileResponse> getSellerPublicProfile(@PathVariable Long id) {
		return ResponseEntity.ok(sellerProfileService.getPublicProfile(id));
	}

	private AppUser getUserFromAuthentication(Authentication authentication) {
		String email = authentication.getName();
		return appUserRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
	}
}
