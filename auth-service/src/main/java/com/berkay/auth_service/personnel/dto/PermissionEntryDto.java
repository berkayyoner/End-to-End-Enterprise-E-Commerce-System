package com.berkay.auth_service.personnel.dto;

import com.berkay.auth_service.personnel.entity.PermissionEntry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PermissionEntryDto(
		@NotBlank @Pattern(regexp = "^P\\d+$", message = "must look like P0, P1, P2, ...") String pageCode,
		boolean canAdd,
		boolean canEdit,
		boolean canDelete) {

	public static PermissionEntryDto from(PermissionEntry entry) {
		return new PermissionEntryDto(entry.getPageCode(), entry.canAdd(), entry.canEdit(), entry.canDelete());
	}
}
