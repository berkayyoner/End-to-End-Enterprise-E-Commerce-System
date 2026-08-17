package com.berkay.product_service.favorite.service;

import com.berkay.product_service.favorite.dto.FavoriteResponse;
import com.berkay.product_service.favorite.entity.Favorite;
import com.berkay.product_service.favorite.repository.FavoriteRepository;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing product favorites.
 * Allows authenticated buyers to favorite and unfavorite products.
 */
@Service
@Transactional
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final ProductRepository productRepository;

	public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository) {
		this.favoriteRepository = favoriteRepository;
		this.productRepository = productRepository;
	}

	/**
	 * Add a product to buyer's favorites.
	 * If already favorited, this is a no-op (idempotent).
	 *
	 * @param productId the product to favorite
	 * @param buyerId the buyer's user ID (from JWT sub)
	 * @return the favorite record
	 * @throws ProductNotFoundException if product doesn't exist
	 */
	public FavoriteResponse addFavorite(Long productId, String buyerId) {
		// Verify product exists
		Product product = productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

		// Check if already favorited, if so return existing
		return favoriteRepository.findByBuyerIdAndProductIdAndDeletedFalse(buyerId, productId)
				.map(FavoriteResponse::from)
				.orElseGet(() -> {
					// Create new favorite
					Favorite favorite = new Favorite(product, buyerId);
					Favorite saved = favoriteRepository.save(favorite);
					return FavoriteResponse.from(saved);
				});
	}

	/**
	 * Remove a product from buyer's favorites.
	 * If not favorited, this is a no-op (idempotent).
	 *
	 * @param productId the product to unfavorite
	 * @param buyerId the buyer's user ID (from JWT sub)
	 */
	public void removeFavorite(Long productId, String buyerId) {
		favoriteRepository.findByBuyerIdAndProductIdAndDeletedFalse(buyerId, productId)
				.ifPresent(favorite -> favorite.softDelete());
	}

	/**
	 * Get all favorites for a buyer.
	 */
	@Transactional(readOnly = true)
	public List<FavoriteResponse> getBuyerFavorites(String buyerId) {
		return favoriteRepository.findByBuyerIdAndDeletedFalse(buyerId).stream()
				.map(FavoriteResponse::from)
				.toList();
	}

	/**
	 * Get the count of favorites for a product.
	 */
	@Transactional(readOnly = true)
	public long getFavoriteCount(Long productId) {
		return favoriteRepository.countByProductIdAndDeletedFalse(productId);
	}
}
