package com.berkay.product_service.campaign.controller;

import com.berkay.product_service.campaign.dto.CampaignRequest;
import com.berkay.product_service.campaign.dto.CampaignResponse;
import com.berkay.product_service.campaign.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Personnel-facing campaign management endpoints for creating, updating, and deleting campaigns,
 * as well as managing product associations. All endpoints require PERM_P6_VIEW/_ADD/_EDIT/_DELETE authorities.
 */
@RestController
@RequestMapping("/personnel/campaigns")
public class PersonnelCampaignController {

	private final CampaignService campaignService;

	public PersonnelCampaignController(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	/**
	 * GET /personnel/campaigns - List all campaigns (not soft-deleted), requires P6_VIEW permission.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P6_VIEW')")
	public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {
		List<CampaignResponse> campaigns = campaignService.getAllCampaigns();
		return ResponseEntity.ok(campaigns);
	}

	/**
	 * POST /personnel/campaigns - Create a new campaign, requires P6_ADD permission.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P6_ADD')")
	public ResponseEntity<CampaignResponse> createCampaign(@Valid @RequestBody CampaignRequest request) {
		CampaignResponse campaign = campaignService.createCampaign(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(campaign);
	}

	/**
	 * PUT /personnel/campaigns/{id} - Update a campaign, requires P6_EDIT permission.
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P6_EDIT')")
	public ResponseEntity<CampaignResponse> updateCampaign(
			@PathVariable Long id,
			@Valid @RequestBody CampaignRequest request) {
		CampaignResponse campaign = campaignService.updateCampaign(id, request);
		return ResponseEntity.ok(campaign);
	}

	/**
	 * DELETE /personnel/campaigns/{id} - Delete (soft-delete) a campaign, requires P6_DELETE permission.
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P6_DELETE')")
	public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
		campaignService.deleteCampaign(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * POST /personnel/campaigns/{campaignId}/products/{productId} - Add a product to a campaign,
	 * requires P6_EDIT permission.
	 */
	@PostMapping("/{campaignId}/products/{productId}")
	@PreAuthorize("hasAuthority('PERM_P6_EDIT')")
	public ResponseEntity<Void> addProductToCampaign(
			@PathVariable Long campaignId,
			@PathVariable Long productId) {
		campaignService.addProductToCampaign(campaignId, productId);
		return ResponseEntity.created(null).build();
	}

	/**
	 * DELETE /personnel/campaigns/{campaignId}/products/{productId} - Remove a product from a campaign,
	 * requires P6_EDIT permission.
	 */
	@DeleteMapping("/{campaignId}/products/{productId}")
	@PreAuthorize("hasAuthority('PERM_P6_EDIT')")
	public ResponseEntity<Void> removeProductFromCampaign(
			@PathVariable Long campaignId,
			@PathVariable Long productId) {
		campaignService.removeProductFromCampaign(campaignId, productId);
		return ResponseEntity.noContent().build();
	}
}
