package com.berkay.auth_service.personnel.dto;

import com.berkay.auth_service.personnel.entity.Personnel;

import java.time.Instant;
import java.util.List;

public record PersonnelResponse(
		Long id,
		String email,
		String firstName,
		String lastName,
		List<String> permissions,
		Instant createdAt) {

	public static PersonnelResponse from(Personnel personnel) {
		return new PersonnelResponse(
				personnel.getId(),
				personnel.getEmail(),
				personnel.getFirstName(),
				personnel.getLastName(),
				personnel.getPermissionGroup().toCodes(),
				personnel.getCreatedAt());
	}
}
