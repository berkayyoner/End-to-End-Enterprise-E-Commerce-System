package com.berkay.product_service.product.dto;

import com.berkay.product_service.product.entity.ProductPhoto;

/**
 * Data transfer object for a product photo response.
 * Contains base64-encoded image data instead of raw bytes for JSON serialization.
 */
public record ProductPhotoDTO(
		Long id,
		String imageDataBase64,
		int displayOrder
) {
	public static ProductPhotoDTO from(ProductPhoto photo) {
		return new ProductPhotoDTO(
				photo.getId(),
				java.util.Base64.getEncoder().encodeToString(photo.getImageData()),
				photo.getDisplayOrder()
		);
	}
}
