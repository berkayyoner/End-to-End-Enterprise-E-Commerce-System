package com.berkay.product_service.favorite.controller;

import com.berkay.product_service.favorite.dto.FavoriteResponse;
import com.berkay.product_service.favorite.service.FavoriteService;
import com.berkay.product_service.product.dto.SimpleProductDTO;
import com.berkay.product_service.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for product favorites.
 * All endpoints require authentication (authenticated buyer).
 */
@RestController
@RequestMapping("/products")
public class FavoriteController {

	private final FavoriteService favoriteService;
	private final ProductService productService;

	public FavoriteController(FavoriteService favoriteService, ProductService productService) {
		this.favoriteService = favoriteService;
		this.productService = productService;
	}

	/**
	 * Add a product to buyer's favorites.
	 * Requires authentication.
	 */
	@PostMapping("/{productId}/favorite")
	public ResponseEntity<FavoriteResponse> addFavorite(
			@PathVariable Long productId,
			Authentication authentication) {
		String buyerId = authentication.getName();
		FavoriteResponse response = favoriteService.addFavorite(productId, buyerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Remove a product from buyer's favorites.
	 * Requires authentication.
	 */
	@DeleteMapping("/{productId}/favorite")
	public ResponseEntity<Void> removeFavorite(
			@PathVariable Long productId,
			Authentication authentication) {
		String buyerId = authentication.getName();
		favoriteService.removeFavorite(productId, buyerId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Get all favorites for authenticated buyer.
	 * Returns full product details for each favorite (simplified product cards).
	 * Requires authentication.
	 */
	@GetMapping("/favorites/me")
	public ResponseEntity<List<SimpleProductDTO>> getMyFavorites(
			Authentication authentication,
			@RequestParam(value = "locale", defaultValue = "tr") String locale) {
		String buyerId = authentication.getName();
		List<FavoriteResponse> favorites = favoriteService.getBuyerFavorites(buyerId);

		// Convert to SimpleProductDTO for frontend product cards
		List<SimpleProductDTO> products = favorites.stream()
				.map(fav -> productService.getSimpleProductDTO(fav.productId(), locale))
				.toList();

		return ResponseEntity.ok(products);
	}
}
