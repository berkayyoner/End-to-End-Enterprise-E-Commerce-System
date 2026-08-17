package com.berkay.auth_service.personnel.dto;

import com.berkay.auth_service.personnel.entity.Personnel;

import java.time.Instant;

public record PersonnelListResponse(
		Long id,
		String email,
		String firstName,
		String lastName,
		Long permissionGroupId,
		String permissionGroupName,
		Instant createdAt) {

	public static PersonnelListResponse from(Personnel personnel) {
		return new PersonnelListResponse(
				personnel.getId(),
				personnel.getEmail(),
				personnel.getFirstName(),
				personnel.getLastName(),
				personnel.getPermissionGroup().getId(),
				personnel.getPermissionGroup().getName(),
				personnel.getCreatedAt());
	}
}
