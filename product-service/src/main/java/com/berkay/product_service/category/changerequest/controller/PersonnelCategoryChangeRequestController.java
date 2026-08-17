package com.berkay.product_service.category.changerequest.controller;

import com.berkay.product_service.category.changerequest.dto.CategoryChangeRequestResponse;
import com.berkay.product_service.category.changerequest.dto.RejectCategoryChangeRequestInput;
import com.berkay.product_service.category.changerequest.service.CategoryChangeRequestService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only endpoints for reviewing and approving/rejecting category change requests.
 * Requires P0 permissions per RULES.md: "a admin needs to accept or deny it for it to be effected".
 */
@RestController
@RequestMapping("/personnel/category-change-requests")
public class PersonnelCategoryChangeRequestController {

	private final CategoryChangeRequestService changeRequestService;

	public PersonnelCategoryChangeRequestController(CategoryChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P0_VIEW')")
	public Page<CategoryChangeRequestResponse> listPending(Pageable pageable) {
		return changeRequestService.listPending(pageable);
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasAuthority('PERM_P0_EDIT')")
	public CategoryChangeRequestResponse approve(@PathVariable Long id, Authentication authentication) {
		return changeRequestService.approve(id, authentication.getName());
	}

	@PostMapping("/{id}/reject")
	@PreAuthorize("hasAuthority('PERM_P0_EDIT')")
	public CategoryChangeRequestResponse reject(@PathVariable Long id,
			@Valid @RequestBody RejectCategoryChangeRequestInput input,
			Authentication authentication) {
		return changeRequestService.reject(id, authentication.getName(), input.reason());
	}
}
