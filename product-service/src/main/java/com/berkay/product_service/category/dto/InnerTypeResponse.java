package com.berkay.product_service.category.dto;

import com.berkay.common.i18n.TranslationResolver;
import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.InnerTypeTranslation;

/**
 * Response containing an InnerType with its resolved translation for the requested locale.
 */
public record InnerTypeResponse(
		Long id,
		Long subTypeId,
		String name,
		String description
) {
	public static InnerTypeResponse from(InnerType innerType, String requestedLocale) {
		InnerTypeTranslation resolved = TranslationResolver.resolve(innerType.getTranslations(), requestedLocale)
				.orElse(null);

		String name = resolved != null ? resolved.getName() : "Untranslated";
		String description = resolved != null ? resolved.getDescription() : null;

		return new InnerTypeResponse(innerType.getId(), innerType.getSubType().getId(), name, description);
	}
}
