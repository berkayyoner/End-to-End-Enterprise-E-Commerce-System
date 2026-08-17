package com.berkay.product_service.product.dto;

import com.berkay.common.i18n.TranslationResolver;
import com.berkay.product_service.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response containing a Product with its resolved translation for the requested locale.
 */
public record ProductResponse(
		Long id,
		Long sellerId,
		Long innerTypeId,
		BigDecimal price,
		int stock,
		String name,
		String shortDescription,
		String longDescription,
		List<String> keyFeatures,
		List<ProductPhotoDTO> photos
) {
	public static ProductResponse from(Product product, String requestedLocale) {
		var resolved = TranslationResolver.resolve(product.getTranslations(), requestedLocale)
				.orElse(null);

		String name = resolved != null ? resolved.getName() : "Untranslated";
		String shortDescription = resolved != null ? resolved.getShortDescription() : null;
		String longDescription = resolved != null ? resolved.getLongDescription() : null;

		List<String> features = product.getKeyFeatures().stream()
				.map(kf -> kf.getFeatureText())
				.toList();

		List<ProductPhotoDTO> photoDTOs = product.getPhotos().stream()
				.sorted((p1, p2) -> Integer.compare(p1.getDisplayOrder(), p2.getDisplayOrder()))
				.map(ProductPhotoDTO::from)
				.toList();

		return new ProductResponse(
				product.getId(),
				product.getSellerId(),
				product.getInnerType().getId(),
				product.getPrice(),
				product.getStock(),
				name,
				shortDescription,
				longDescription,
				features,
				photoDTOs
		);
	}
}
