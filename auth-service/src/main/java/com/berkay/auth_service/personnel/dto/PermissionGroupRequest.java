package com.berkay.auth_service.personnel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PermissionGroupRequest(
		@NotBlank @Size(max = 100) String name,
		@NotNull List<@Valid PermissionEntryDto> entries) {
}
