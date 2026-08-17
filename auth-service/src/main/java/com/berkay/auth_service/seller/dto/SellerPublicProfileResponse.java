package com.berkay.auth_service.seller.dto;

import com.berkay.auth_service.user.entity.AppUser;

/**
 * Response for public access to a seller's profile (GET /sellers/{id}/public-profile).
 * Contains only store name and follower count - no earnings data.
 */
public record SellerPublicProfileResponse(
		Long id,
		String storeName,
		int followerCount) {

	public static SellerPublicProfileResponse from(AppUser user) {
		return new SellerPublicProfileResponse(
				user.getId(),
				user.getStoreName(),
				user.getFollowerCount());
	}
}
