package com.berkay.product_service.campaign.dto;

import com.berkay.product_service.campaign.entity.Campaign;

import java.time.LocalDateTime;

/**
 * Response DTO for a campaign, used in product detail and campaign listing endpoints.
 */
public record CampaignResponse(
		Long id,
		String name,
		String description,
		LocalDateTime startDate,
		LocalDateTime endDate,
		boolean active,
		boolean currentlyActive
) {

	public static CampaignResponse from(Campaign campaign) {
		return new CampaignResponse(
				campaign.getId(),
				campaign.getName(),
				campaign.getDescription(),
				campaign.getStartDate(),
				campaign.getEndDate(),
				campaign.isActive(),
				campaign.isCurrentlyActive()
		);
	}
}
