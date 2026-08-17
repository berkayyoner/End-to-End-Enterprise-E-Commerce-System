package com.berkay.product_service.campaign.service;

import com.berkay.product_service.campaign.dto.CampaignRequest;
import com.berkay.product_service.campaign.dto.CampaignResponse;
import com.berkay.product_service.campaign.entity.Campaign;
import com.berkay.product_service.campaign.entity.CampaignProduct;
import com.berkay.product_service.campaign.exception.CampaignNotFoundException;
import com.berkay.product_service.campaign.repository.CampaignProductRepository;
import com.berkay.product_service.campaign.repository.CampaignRepository;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing campaigns and their associations with products.
 */
@Service
public class CampaignService {

	private final CampaignRepository campaignRepository;
	private final CampaignProductRepository campaignProductRepository;
	private final ProductRepository productRepository;

	public CampaignService(
			CampaignRepository campaignRepository,
			CampaignProductRepository campaignProductRepository,
			ProductRepository productRepository) {
		this.campaignRepository = campaignRepository;
		this.campaignProductRepository = campaignProductRepository;
		this.productRepository = productRepository;
	}

	@Transactional(readOnly = true)
	public CampaignResponse getCampaignById(Long id) {
		Campaign campaign = campaignRepository.findActiveById(id)
				.orElseThrow(() -> new CampaignNotFoundException("Campaign not found with id: " + id));
		return CampaignResponse.from(campaign);
	}

	@Transactional(readOnly = true)
	public List<CampaignResponse> getAllCampaigns() {
		return campaignRepository.findAllActive().stream()
				.map(CampaignResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<CampaignResponse> getActiveCampaigns() {
		return campaignRepository.findAllCurrentlyActive().stream()
				.map(CampaignResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<CampaignResponse> getCampaignsByProduct(Long productId) {
		// Verify product exists
		productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		return campaignProductRepository.findActiveByProductId(productId).stream()
				.map(cp -> CampaignResponse.from(cp.getCampaign()))
				.toList();
	}

	@Transactional
	public CampaignResponse createCampaign(CampaignRequest request) {
		Campaign campaign = new Campaign(
				request.name(),
				request.description(),
				request.startDate(),
				request.endDate(),
				request.active()
		);
		Campaign saved = campaignRepository.save(campaign);
		return CampaignResponse.from(saved);
	}

	@Transactional
	public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
		Campaign campaign = campaignRepository.findActiveById(id)
				.orElseThrow(() -> new CampaignNotFoundException("Campaign not found with id: " + id));

		campaign.setName(request.name());
		campaign.setDescription(request.description());
		campaign.setStartDate(request.startDate());
		campaign.setEndDate(request.endDate());
		campaign.setActive(request.active());

		Campaign updated = campaignRepository.save(campaign);
		return CampaignResponse.from(updated);
	}

	@Transactional
	public void deleteCampaign(Long id) {
		Campaign campaign = campaignRepository.findActiveById(id)
				.orElseThrow(() -> new CampaignNotFoundException("Campaign not found with id: " + id));

		campaign.softDelete();
		campaignRepository.save(campaign);
	}

	@Transactional
	public void addProductToCampaign(Long campaignId, Long productId) {
		Campaign campaign = campaignRepository.findActiveById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException("Campaign not found with id: " + campaignId));

		Product product = productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		// Check if association already exists
		if (campaignProductRepository.existsByCampaignAndProduct(campaignId, productId)) {
			return;  // Already associated, silently ignore
		}

		CampaignProduct campaignProduct = new CampaignProduct(campaign, product);
		campaignProductRepository.save(campaignProduct);
	}

	@Transactional
	public void removeProductFromCampaign(Long campaignId, Long productId) {
		CampaignProduct campaignProduct = campaignProductRepository.findByCampaignAndProduct(campaignId, productId)
				.orElseThrow(() -> new CampaignNotFoundException("Campaign-Product association not found"));

		campaignProduct.softDelete();
		campaignProductRepository.save(campaignProduct);
	}
}
