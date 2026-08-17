package com.berkay.product_service.campaign.exception;

/**
 * Exception thrown when a campaign is not found.
 */
public class CampaignNotFoundException extends CampaignException {

	public CampaignNotFoundException(String message) {
		super(message);
	}
}
