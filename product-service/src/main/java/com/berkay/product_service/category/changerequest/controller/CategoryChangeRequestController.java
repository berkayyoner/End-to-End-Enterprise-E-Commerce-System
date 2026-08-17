package com.berkay.product_service.category.changerequest.controller;

import com.berkay.product_service.category.changerequest.dto.CategoryChangeRequestInput;
import com.berkay.product_service.category.changerequest.dto.CategoryChangeRequestResponse;
import com.berkay.product_service.category.changerequest.service.CategoryChangeRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public endpoint for submitting category change requests. Requires PERM_P4_EDIT authority
 * (the same permission moderators already need to edit categories directly, per task 2.1).
 */
@RestController
@RequestMapping("/categories/change-requests")
public class CategoryChangeRequestController {

	private final CategoryChangeRequestService changeRequestService;

	public CategoryChangeRequestController(CategoryChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P4_EDIT')")
	public ResponseEntity<CategoryChangeRequestResponse> submit(
			@Valid @RequestBody CategoryChangeRequestInput input) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(changeRequestService.submit(input));
	}
}
