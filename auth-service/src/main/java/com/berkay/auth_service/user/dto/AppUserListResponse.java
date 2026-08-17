package com.berkay.auth_service.user.dto;

import com.berkay.auth_service.user.entity.AppUser;

import java.time.Instant;

public record AppUserListResponse(
		Long id,
		String email,
		String firstName,
		String lastName,
		String phoneNumber,
		boolean idVerified,
		boolean seller,
		boolean banned,
		Instant createdAt
) {

	public static AppUserListResponse from(AppUser user, boolean banned) {
		return new AppUserListResponse(
				user.getId(),
				user.getEmail(),
				user.getFirstName(),
				user.getLastName(),
				user.getPhoneNumber(),
				user.isIdVerified(),
				user.isSeller(),
				banned,
				user.getCreatedAt()
		);
	}
}
