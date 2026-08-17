package com.berkay.auth_service.sellerapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerApplicationRequest(
		@NotBlank @Size(max = 200) String companyName,
		@NotBlank @Size(max = 50) String taxId,
		@NotBlank @Size(max = 32) String companyPhone,
		@NotBlank @Size(max = 500) String companyAddress) {
}
