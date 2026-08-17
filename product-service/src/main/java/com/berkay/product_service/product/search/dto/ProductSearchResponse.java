package com.berkay.product_service.product.search.dto;

import com.berkay.product_service.product.search.document.ProductDocument;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for a single product in search results.
 */
public record ProductSearchResponse(
		Long id,
		Long sellerId,
		Long innerTypeId,
		Long subTypeId,
		Long mainCategoryId,
		BigDecimal price,
		int stock,
		String nameEn,
		String nameTr,
		Instant createdAt,
		int salesCount,
		int favoriteCount,
		BigDecimal averageRating
) {
	public static ProductSearchResponse from(ProductDocument doc) {
		return new ProductSearchResponse(
				doc.getId(),
				doc.getSellerId(),
				doc.getInnerTypeId(),
				doc.getSubTypeId(),
				doc.getMainCategoryId(),
				doc.getPrice(),
				doc.getStock(),
				doc.getNameEn(),
				doc.getNameTr(),
				doc.getCreatedAt(),
				doc.getSalesCount(),
				doc.getFavoriteCount(),
				doc.getAverageRating()
		);
	}
}
