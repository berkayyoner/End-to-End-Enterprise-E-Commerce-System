package com.berkay.product_service.campaign.exception;

/**
 * Base exception for campaign-related errors.
 */
public class CampaignException extends RuntimeException {

	public CampaignException(String message) {
		super(message);
	}

	public CampaignException(String message, Throwable cause) {
		super(message, cause);
	}
}
