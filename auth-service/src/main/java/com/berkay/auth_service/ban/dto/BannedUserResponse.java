package com.berkay.auth_service.ban.dto;

import com.berkay.auth_service.ban.entity.BannedUser;

import java.time.Instant;

public record BannedUserResponse(
		Long id,
		Long originalAppUserId,
		String email,
		String phoneNumber,
		String idNumbers,
		String ipAddress,
		String reason,
		String bannedByEmail,
		Instant bannedAt) {

	public static BannedUserResponse from(BannedUser bannedUser) {
		return new BannedUserResponse(
				bannedUser.getId(),
				bannedUser.getOriginalAppUserId(),
				bannedUser.getEmail(),
				bannedUser.getPhoneNumber(),
				bannedUser.getIdNumbers(),
				bannedUser.getIpAddress(),
				bannedUser.getReason(),
				bannedUser.getCreatedBy(),
				bannedUser.getCreatedAt());
	}
}
