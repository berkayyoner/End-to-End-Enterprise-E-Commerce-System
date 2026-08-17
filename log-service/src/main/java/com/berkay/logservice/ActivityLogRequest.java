package com.berkay.logservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActivityLogRequest(
		@NotNull ActorType actorType,
		Long actorId,
		@NotBlank @Size(max = 60) String sourceService,
		@NotBlank @Size(max = 100) String action,
		@Size(max = 2000) String details,
		@Size(max = 64) String ipAddress) {
}
