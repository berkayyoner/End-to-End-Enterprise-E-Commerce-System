package com.berkay.product_service.category.changerequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectCategoryChangeRequestInput(
		@NotBlank(message = "reason must not be blank")
		@Size(max = 500, message = "reason must not exceed 500 characters")
		String reason
) {
}
