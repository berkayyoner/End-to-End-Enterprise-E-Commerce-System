package com.berkay.product_service.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request to create or update a Product with translations, key features, and photos.
 */
public record ProductRequest(
		@NotNull(message = "innerTypeId cannot be null")
		Long innerTypeId,

		@NotNull(message = "price cannot be null")
		@DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
		BigDecimal price,

		@NotNull(message = "stock cannot be null")
		@Min(value = 0, message = "stock cannot be negative")
		int stock,

		@NotEmpty(message = "translations list cannot be empty")
		@Valid
		List<ProductTranslationInput> translations,

		@Valid
		List<String> keyFeatures,

		@Valid
		List<ProductPhotoUpload> photos
) {
}
