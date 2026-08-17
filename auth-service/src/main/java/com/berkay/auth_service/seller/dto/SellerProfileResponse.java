package com.berkay.auth_service.seller.dto;

import com.berkay.auth_service.user.entity.AppUser;

import java.math.BigDecimal;

/**
 * Response for authenticated seller getting their own profile (GET /sellers/me).
 * Contains store name, earned money, and follower count.
 */
public record SellerProfileResponse(
		Long id,
		String email,
		String storeName,
		BigDecimal totalEarned,
		int followerCount) {

	public static SellerProfileResponse from(AppUser user, BigDecimal totalEarned) {
		return new SellerProfileResponse(
				user.getId(),
				user.getEmail(),
				user.getStoreName(),
				totalEarned != null ? totalEarned : BigDecimal.ZERO,
				user.getFollowerCount());
	}
}
