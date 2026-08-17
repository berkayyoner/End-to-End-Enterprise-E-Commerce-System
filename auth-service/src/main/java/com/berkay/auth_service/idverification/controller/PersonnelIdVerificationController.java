package com.berkay.auth_service.idverification.controller;

import com.berkay.auth_service.idverification.dto.IdVerificationRejectRequest;
import com.berkay.auth_service.idverification.dto.IdVerificationResponse;
import com.berkay.auth_service.idverification.entity.IdVerificationStatus;
import com.berkay.auth_service.idverification.service.IdVerificationService;
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
 * RULES.md's "ID Applications" personnel page (PERMISSIONS.md: P1). Approving here is what
 * flips AppUser#idVerified, unblocking Buy Now/Add to Basket/Sell on Berkay.
 */
@RestController
@RequestMapping("/personnel/id-verifications")
public class PersonnelIdVerificationController {

	private final IdVerificationService idVerificationService;

	public PersonnelIdVerificationController(IdVerificationService idVerificationService) {
		this.idVerificationService = idVerificationService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P1_VIEW')")
	public Page<IdVerificationResponse> list(
			@RequestParam(defaultValue = "PENDING") IdVerificationStatus status,
			Pageable pageable) {
		return idVerificationService.list(status, pageable);
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasAuthority('PERM_P1_EDIT')")
	public IdVerificationResponse approve(@PathVariable Long id, Authentication authentication) {
		return idVerificationService.approve(id, authentication.getName());
	}

	@PostMapping("/{id}/reject")
	@PreAuthorize("hasAuthority('PERM_P1_EDIT')")
	public IdVerificationResponse reject(@PathVariable Long id, @Valid @RequestBody IdVerificationRejectRequest request,
			Authentication authentication) {
		return idVerificationService.reject(id, authentication.getName(), request.reason());
	}
}
