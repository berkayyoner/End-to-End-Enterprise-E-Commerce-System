package com.berkay.product_service.favorite.dto;

import com.berkay.product_service.favorite.entity.Favorite;

public record FavoriteResponse(
		Long id,
		Long productId,
		String buyerId) {

	public static FavoriteResponse from(Favorite favorite) {
		return new FavoriteResponse(
				favorite.getId(),
				favorite.getProduct().getId(),
				favorite.getBuyerId()
		);
	}
}
