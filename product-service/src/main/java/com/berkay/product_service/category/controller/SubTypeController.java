package com.berkay.product_service.category.controller;

import com.berkay.product_service.category.dto.SubTypeRequest;
import com.berkay.product_service.category.dto.SubTypeResponse;
import com.berkay.product_service.category.service.SubTypeService;
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
 * REST endpoints for SubType CRUD. Write operations require PERM_P4_* authorities.
 * GET/list are public (no auth required).
 */
@RestController
@RequestMapping("/categories/sub")
public class SubTypeController {

	private final SubTypeService subTypeService;

	public SubTypeController(SubTypeService subTypeService) {
		this.subTypeService = subTypeService;
	}

	@GetMapping
	public List<SubTypeResponse> list(
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return subTypeService.list(locale);
	}

	@GetMapping("/{id}")
	public SubTypeResponse get(
			@PathVariable Long id,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return subTypeService.get(id, locale);
	}

	@GetMapping("/by-main/{mainCategoryId}")
	public List<SubTypeResponse> listByMainCategory(
			@PathVariable Long mainCategoryId,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return subTypeService.listByMainCategory(mainCategoryId, locale);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P4_ADD')")
	public ResponseEntity<SubTypeResponse> create(
			@Valid @RequestBody SubTypeRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(subTypeService.create(request));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P4_EDIT')")
	public SubTypeResponse update(
			@PathVariable Long id,
			@Valid @RequestBody SubTypeRequest request) {
		return subTypeService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P4_DELETE')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		subTypeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
