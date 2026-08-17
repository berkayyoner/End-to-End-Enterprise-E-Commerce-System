package com.berkay.product_service.product.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comprehensive product detail response aggregating core product data with related recommendations,
 * reviews, campaigns, and marketplace information.
 *
 * Placeholder fields (with zero/empty values) that will be populated by Phase 6.1/6.2:
 * - averageRating, ratingCount, qna: Real Review/Q&A entities will populate these without reshaping.
 * - campaigns: Campaign entity association, populated by Phase 6.4.
 *
 * Simple-query fields (same-category or random):
 * - similarProducts, recommendedProducts, boughtTogetherProducts: Same InnerType category.
 * - mightAlsoInterestYou: Product names from same category.
 * - popularBrandsOrStores: Distinct seller IDs from same category.
 * - popularPages: Product names/IDs from same category.
 * - estimatedDeliveryDays: Fixed dummy range (3-7 days, no real distance calculation).
 */
public record ProductDetailDTO(
		Long id,
		Long sellerId,
		Long innerTypeId,
		BigDecimal price,
		int stock,
		String name,
		String shortDescription,
		String longDescription,
		List<String> keyFeatures,
		List<ProductPhotoDTO> photos,

		// Phase 6.1/6.2 placeholders: zero/empty values, real entities will populate these
		double averageRating,
		int ratingCount,
		List<Object> qna,  // Will be replaced with real QnaDTO list in Phase 6.2

		// Phase 6.4 placeholder
		List<Object> campaigns,  // Will be replaced with real CampaignDTO list in Phase 6.4

		// Simple same-category queries
		List<SimpleProductDTO> similarProducts,
		List<SimpleProductDTO> recommendedProducts,
		List<SimpleProductDTO> boughtTogetherProducts,

		// Text-only and name-only lists
		List<ProductNameDTO> mightAlsoInterestYou,
		List<SellerBrandDTO> popularBrandsOrStores,
		List<ProductNameDTO> popularPages,

		// Fixed delivery estimate
		String estimatedDeliveryDays
) {
}
