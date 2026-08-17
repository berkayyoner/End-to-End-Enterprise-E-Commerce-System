package com.berkay.product_service.category.controller;

import com.berkay.product_service.category.dto.InnerTypeRequest;
import com.berkay.product_service.category.dto.InnerTypeResponse;
import com.berkay.product_service.category.service.InnerTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for InnerType CRUD. Write operations require PERM_P4_* authorities.
 * GET/list are public (no auth required).
 */
@RestController
@RequestMapping("/categories/inner")
public class InnerTypeController {

	private final InnerTypeService innerTypeService;

	public InnerTypeController(InnerTypeService innerTypeService) {
		this.innerTypeService = innerTypeService;
	}

	@GetMapping
	public List<InnerTypeResponse> list(
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return innerTypeService.list(locale);
	}

	@GetMapping("/{id}")
	public InnerTypeResponse get(
			@PathVariable Long id,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return innerTypeService.get(id, locale);
	}

	@GetMapping("/by-sub/{subTypeId}")
	public List<InnerTypeResponse> listBySubType(
			@PathVariable Long subTypeId,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return innerTypeService.listBySubType(subTypeId, locale);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P4_ADD')")
	public ResponseEntity<InnerTypeResponse> create(
			@Valid @RequestBody InnerTypeRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(innerTypeService.create(request));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P4_EDIT')")
	public InnerTypeResponse update(
			@PathVariable Long id,
			@Valid @RequestBody InnerTypeRequest request) {
		return innerTypeService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P4_DELETE')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		innerTypeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
