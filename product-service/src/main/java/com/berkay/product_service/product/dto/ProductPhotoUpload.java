package com.berkay.product_service.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request input for a product photo upload (base64-encoded image data).
 */
public record ProductPhotoUpload(
		@NotBlank(message = "imageDataBase64 cannot be blank")
		String imageDataBase64,

		@jakarta.validation.constraints.Min(value = 0, message = "displayOrder cannot be negative")
		@jakarta.validation.constraints.Max(value = 9, message = "displayOrder cannot exceed 9")
		int displayOrder
) {
}
