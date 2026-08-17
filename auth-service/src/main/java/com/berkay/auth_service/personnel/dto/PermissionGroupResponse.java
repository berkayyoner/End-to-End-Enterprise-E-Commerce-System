package com.berkay.auth_service.personnel.dto;

import com.berkay.auth_service.personnel.entity.PermissionGroup;

import java.time.Instant;
import java.util.List;

public record PermissionGroupResponse(
		Long id,
		String name,
		List<PermissionEntryDto> entries,
		List<String> codes,
		Instant createdAt,
		Instant updatedAt) {

	public static PermissionGroupResponse from(PermissionGroup group) {
		return new PermissionGroupResponse(
				group.getId(),
				group.getName(),
				group.getEntries().stream().map(PermissionEntryDto::from).toList(),
				group.toCodes(),
				group.getCreatedAt(),
				group.getUpdatedAt());
	}
}
