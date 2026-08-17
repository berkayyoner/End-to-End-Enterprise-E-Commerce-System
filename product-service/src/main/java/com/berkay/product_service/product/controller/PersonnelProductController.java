package com.berkay.product_service.product.controller;

import com.berkay.product_service.product.dto.ProductRequest;
import com.berkay.product_service.product.dto.ProductResponse;
import com.berkay.product_service.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for Product management by personnel (admin/moderator).
 * All endpoints require corresponding P5 permissions.
 * Unlike seller endpoints, these do not enforce ownership - personnel can manage any product.
 */
@RestController
@RequestMapping("/personnel/products")
public class PersonnelProductController {

	private final ProductService productService;

	public PersonnelProductController(ProductService productService) {
		this.productService = productService;
	}

	/**
	 * List all products for personnel review/management.
	 * Requires PERM_P5_VIEW authority.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P5_VIEW')")
	public List<ProductResponse> list(
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return productService.listAllForPersonnel(locale);
	}

	/**
	 * Update any product's editable fields (price, stock, translations, photos, key features).
	 * Requires PERM_P5_EDIT authority.
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P5_EDIT')")
	public ProductResponse update(
			@PathVariable Long id,
			@Valid @RequestBody ProductRequest request) {
		return productService.updateForPersonnel(id, request);
	}

	/**
	 * Soft-delete any product.
	 * Requires PERM_P5_DELETE authority.
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P5_DELETE')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.deleteForPersonnel(id);
		return ResponseEntity.noContent().build();
	}
}
