package com.berkay.product_service.campaign.repository;

import com.berkay.product_service.campaign.entity.CampaignProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignProductRepository extends JpaRepository<CampaignProduct, Long> {

	/**
	 * Find all campaign-product associations for a given product that are not soft-deleted.
	 */
	@Query("SELECT cp FROM CampaignProduct cp WHERE cp.product.id = :productId AND cp.deleted = false")
	List<CampaignProduct> findByProductId(Long productId);

	/**
	 * Find all active campaigns for a given product (campaign must be not soft-deleted, active, and within date range).
	 */
	@Query("SELECT cp FROM CampaignProduct cp WHERE cp.product.id = :productId AND cp.deleted = false " +
			"AND cp.campaign.active = true " +
			"AND cp.campaign.startDate <= CURRENT_TIMESTAMP AND cp.campaign.endDate >= CURRENT_TIMESTAMP " +
			"ORDER BY cp.campaign.createdAt DESC")
	List<CampaignProduct> findActiveByProductId(Long productId);

	/**
	 * Check if a campaign-product association exists (not soft-deleted).
	 */
	@Query("SELECT COUNT(cp) > 0 FROM CampaignProduct cp WHERE cp.campaign.id = :campaignId AND cp.product.id = :productId AND cp.deleted = false")
	boolean existsByCampaignAndProduct(Long campaignId, Long productId);

	/**
	 * Find a campaign-product association by campaign and product IDs (not soft-deleted).
	 */
	@Query("SELECT cp FROM CampaignProduct cp WHERE cp.campaign.id = :campaignId AND cp.product.id = :productId AND cp.deleted = false")
	Optional<CampaignProduct> findByCampaignAndProduct(Long campaignId, Long productId);
}
