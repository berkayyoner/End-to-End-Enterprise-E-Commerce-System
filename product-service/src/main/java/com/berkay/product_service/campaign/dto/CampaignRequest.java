package com.berkay.product_service.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request DTO for creating or updating a campaign.
 */
public record CampaignRequest(
		@NotBlank(message = "Campaign name is required")
		String name,

		String description,

		@NotNull(message = "Start date is required")
		LocalDateTime startDate,

		@NotNull(message = "End date is required")
		LocalDateTime endDate,

		@NotNull(message = "Active flag is required")
		Boolean active
) {
}
