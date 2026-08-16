package com.berkay.auth_service.sellerapplication.controller;

import com.berkay.auth_service.sellerapplication.dto.SellerApplicationRejectRequest;
import com.berkay.auth_service.sellerapplication.dto.SellerApplicationResponse;
import com.berkay.auth_service.sellerapplication.entity.SellerApplicationStatus;
import com.berkay.auth_service.sellerapplication.service.SellerApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RULES.md's "Seller Applications" personnel page (PERMISSIONS.md: P2). Approving here is what
 * flips AppUser#seller, unlocking "My Store"/"Add New Product" (Phase 4).
 */
@RestController
@RequestMapping("/personnel/seller-applications")
public class PersonnelSellerApplicationController {

	private final SellerApplicationService sellerApplicationService;

	public PersonnelSellerApplicationController(SellerApplicationService sellerApplicationService) {
		this.sellerApplicationService = sellerApplicationService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P2_VIEW')")
	public Page<SellerApplicationResponse> list(
			@RequestParam(defaultValue = "PENDING") SellerApplicationStatus status,
			Pageable pageable) {
		return sellerApplicationService.list(status, pageable);
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasAuthority('PERM_P2_EDIT')")
	public SellerApplicationResponse approve(@PathVariable Long id, Authentication authentication) {
		return sellerApplicationService.approve(id, authentication.getName());
	}

	@PostMapping("/{id}/reject")
	@PreAuthorize("hasAuthority('PERM_P2_EDIT')")
	public SellerApplicationResponse reject(@PathVariable Long id,
			@Valid @RequestBody SellerApplicationRejectRequest request, Authentication authentication) {
		return sellerApplicationService.reject(id, authentication.getName(), request.reason());
	}
}
