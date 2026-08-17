package com.berkay.product_service.product.controller;

import com.berkay.product_service.product.dto.ProductDetailDTO;
import com.berkay.product_service.product.dto.ProductRequest;
import com.berkay.product_service.product.dto.ProductResponse;
import com.berkay.product_service.product.service.ProductDetailService;
import com.berkay.product_service.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
 * REST endpoints for Product CRUD and detail. Create/update/delete require seller authentication.
 * GET/list/detail are public (no auth required).
 */
@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;
	private final ProductDetailService productDetailService;

	public ProductController(ProductService productService, ProductDetailService productDetailService) {
		this.productService = productService;
		this.productDetailService = productDetailService;
	}

	@GetMapping
	public List<ProductResponse> list(
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return productService.list(locale);
	}

	@GetMapping("/{id}")
	public ProductResponse get(
			@PathVariable Long id,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return productService.get(id, locale);
	}

	@GetMapping("/{id}/detail")
	public ProductDetailDTO getDetail(
			@PathVariable Long id,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return productDetailService.getProductDetail(id, locale);
	}

	@GetMapping("/by-seller/{sellerId}")
	public List<ProductResponse> listBySeller(
			@PathVariable Long sellerId,
			@RequestParam(name = "locale", defaultValue = "tr") String locale) {
		return productService.listBySeller(sellerId, locale);
	}

	@PostMapping
	public ResponseEntity<ProductResponse> create(
			Authentication authentication,
			@Valid @RequestBody ProductRequest request) {
		Long sellerId = extractUserIdFromToken(authentication);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(productService.create(sellerId, request));
	}

	@PutMapping("/{id}")
	public ProductResponse update(
			@PathVariable Long id,
			Authentication authentication,
			@Valid @RequestBody ProductRequest request) {
		Long sellerId = extractUserIdFromToken(authentication);
		return productService.update(id, sellerId, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
			@PathVariable Long id,
			Authentication authentication) {
		Long sellerId = extractUserIdFromToken(authentication);
		productService.delete(id, sellerId);
		return ResponseEntity.noContent().build();
	}

	private Long extractUserIdFromToken(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) {
			throw new IllegalArgumentException("User not authenticated");
		}
		// The subject of the JWT is the user ID
		return Long.parseLong(authentication.getName());
	}
}
