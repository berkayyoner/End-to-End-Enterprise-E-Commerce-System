package com.berkay.product_service.category.controller;

import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.MainCategoryResponse;
import com.berkay.product_service.category.service.MainCategoryService;
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
 * REST endpoints for MainCategory CRUD. Write operations require PERM_P4_* authorities.
 * GET/list are public (no auth required).
 */
@RestController
@RequestMapping("/categories/main")
public class MainCategoryController {

	private final MainCategoryService mainCategoryService;

	public MainCategoryController(MainCategoryService mainCategoryService) {
		this.mainCategoryService = mainCategoryService;
	}

	@GetMapping
	public List<MainCategoryResponse> list(
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return mainCategoryService.list(locale);
	}

	@GetMapping("/{id}")
	public MainCategoryResponse get(
			@PathVariable Long id,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return mainCategoryService.get(id, locale);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P4_ADD')")
	public ResponseEntity<MainCategoryResponse> create(
			@Valid @RequestBody MainCategoryRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(mainCategoryService.create(request));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P4_EDIT')")
	public MainCategoryResponse update(
			@PathVariable Long id,
			@Valid @RequestBody MainCategoryRequest request) {
		return mainCategoryService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P4_DELETE')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		mainCategoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
