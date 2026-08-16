package com.berkay.auth_service.user.dto;

import com.berkay.auth_service.user.entity.AppUser;

import java.time.Instant;

public record RegisterResponse(
		Long id,
		String email,
		String firstName,
		String lastName,
		String phoneNumber,
		boolean idVerified,
		boolean seller,
		Instant createdAt) {

	public static RegisterResponse from(AppUser user) {
		return new RegisterResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
				user.getPhoneNumber(), user.isIdVerified(), user.isSeller(), user.getCreatedAt());
	}
}
