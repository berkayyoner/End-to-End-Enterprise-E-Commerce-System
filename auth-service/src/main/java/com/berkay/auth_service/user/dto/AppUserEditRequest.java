package com.berkay.auth_service.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AppUserEditRequest(
		@NotBlank(message = "First name is required")
		String firstName,

		@NotBlank(message = "Last name is required")
		String lastName,

		String phoneNumber
) {
}
