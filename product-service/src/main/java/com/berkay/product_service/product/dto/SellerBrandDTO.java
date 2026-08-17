package com.berkay.product_service.product.dto;

/**
 * Seller/brand representation for "popular brands/stores" list.
 * Currently uses seller ID as the placeholder string field.
 * TODO (Phase 4.1): Once a real seller/store-name field exists in auth-service's AppUser/Seller model,
 * replace the name field with the actual store name instead of seller ID.
 */
public record SellerBrandDTO(
		Long sellerId,
		String name  // Currently placeholder: seller ID as string; will be store name in Phase 4.1
) {
}
