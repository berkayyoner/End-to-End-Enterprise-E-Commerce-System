package com.berkay.product_service.product.dto;

/**
 * Simple product name for "might also interest you" and "popular pages" lists.
 */
public record ProductNameDTO(
		Long id,
		String name
) {
}
