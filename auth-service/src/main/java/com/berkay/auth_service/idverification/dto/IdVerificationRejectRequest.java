package com.berkay.auth_service.idverification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IdVerificationRejectRequest(@NotBlank @Size(max = 500) String reason) {
}
