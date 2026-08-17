package com.berkay.auth_service.personnel.dto;

import jakarta.validation.constraints.NotNull;

public record PermissionGroupAssignmentRequest(
		@NotNull(message = "Permission group ID is required")
		Long permissionGroupId) {
}
