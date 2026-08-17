package com.berkay.product_service.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request to create or update an InnerType with translations.
 */
public record InnerTypeRequest(
		@NotNull(message = "subTypeId cannot be null")
		Long subTypeId,

		@NotEmpty(message = "translations list cannot be empty")
		@Valid
		List<TranslationInput> translations
) {
}
