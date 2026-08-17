package com.berkay.auth_service.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStoreNameRequest(
		@NotBlank(message = "Store name cannot be blank")
		@Size(min = 1, max = 255, message = "Store name must be between 1 and 255 characters")
		String storeName) {
}
