package com.berkay.product_service.campaign.repository;

import com.berkay.product_service.campaign.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

	/**
	 * Find a campaign by ID that is not soft-deleted.
	 */
	@Query("SELECT c FROM Campaign c WHERE c.id = :id AND c.deleted = false")
	Optional<Campaign> findActiveById(Long id);

	/**
	 * Find all non-deleted campaigns.
	 */
	@Query("SELECT c FROM Campaign c WHERE c.deleted = false ORDER BY c.createdAt DESC")
	List<Campaign> findAllActive();

	/**
	 * Find all non-deleted campaigns that are currently active (active flag = true and within date range).
	 */
	@Query("SELECT c FROM Campaign c WHERE c.deleted = false AND c.active = true " +
			"AND c.startDate <= CURRENT_TIMESTAMP AND c.endDate >= CURRENT_TIMESTAMP " +
			"ORDER BY c.createdAt DESC")
	List<Campaign> findAllCurrentlyActive();
}
