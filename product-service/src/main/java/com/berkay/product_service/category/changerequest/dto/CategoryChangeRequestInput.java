package com.berkay.product_service.category.changerequest.dto;

import com.berkay.product_service.category.changerequest.entity.CategoryLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryChangeRequestInput(
		@NotNull(message = "categoryLevel must not be null")
		CategoryLevel categoryLevel,

		Long targetEntityId,

		@NotBlank(message = "nameTranslationTr must not be blank")
		@Size(max = 255, message = "nameTranslationTr must not exceed 255 characters")
		String nameTranslationTr,

		@NotBlank(message = "nameTranslationEn must not be blank")
		@Size(max = 255, message = "nameTranslationEn must not exceed 255 characters")
		String nameTranslationEn,

		@Size(max = 1000, message = "descriptionTranslationTr must not exceed 1000 characters")
		String descriptionTranslationTr,

		@Size(max = 1000, message = "descriptionTranslationEn must not exceed 1000 characters")
		String descriptionTranslationEn
) {
}
