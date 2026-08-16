package com.berkay.auth_service.sellerapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerApplicationRejectRequest(@NotBlank @Size(max = 500) String reason) {
}
