package com.berkay.product_service.campaign.controller;

import com.berkay.product_service.campaign.dto.CampaignResponse;
import com.berkay.product_service.campaign.service.CampaignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public campaign endpoints for retrieving active campaigns and campaign data on product details.
 */
@RestController
@RequestMapping("/campaigns")
public class CampaignController {

	private final CampaignService campaignService;

	public CampaignController(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	/**
	 * GET /campaigns - List all currently active campaigns (public endpoint).
	 */
	@GetMapping
	public ResponseEntity<List<CampaignResponse>> getActiveCampaigns() {
		List<CampaignResponse> campaigns = campaignService.getActiveCampaigns();
		return ResponseEntity.ok(campaigns);
	}

	/**
	 * GET /campaigns/{id} - Get a specific campaign by ID (public endpoint).
	 */
	@GetMapping("/{id}")
	public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable Long id) {
		CampaignResponse campaign = campaignService.getCampaignById(id);
		return ResponseEntity.ok(campaign);
	}

	/**
	 * GET /products/{productId}/campaigns - Get all active campaigns for a specific product (public endpoint).
	 * Used by the product detail page to display relevant campaigns.
	 */
	@GetMapping("/product/{productId}")
	public ResponseEntity<List<CampaignResponse>> getCampaignsByProduct(@PathVariable Long productId) {
		List<CampaignResponse> campaigns = campaignService.getCampaignsByProduct(productId);
		return ResponseEntity.ok(campaigns);
	}
}
