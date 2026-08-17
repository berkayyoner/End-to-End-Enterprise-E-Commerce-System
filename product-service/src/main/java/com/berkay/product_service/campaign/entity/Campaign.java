package com.berkay.product_service.campaign.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Campaign entity representing a promotional campaign that can be associated with multiple products.
 * A campaign is a simple "featured/promoted" concept with a name, description, date range, and active flag.
 * Extends BaseEntity for soft-delete and audit columns.
 */
@Entity
@Table(name = "campaign")
public class Campaign extends BaseEntity {

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", columnDefinition = "CLOB")
	private String description;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "active", nullable = false)
	private boolean active;

	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampaignProduct> products = new ArrayList<>();

	protected Campaign() {
	}

	public Campaign(String name, String description, LocalDateTime startDate, LocalDateTime endDate, boolean active) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<CampaignProduct> getProducts() {
		return List.copyOf(products);
	}

	public void addProduct(CampaignProduct campaignProduct) {
		products.add(campaignProduct);
	}

	public void removeProduct(CampaignProduct campaignProduct) {
		products.remove(campaignProduct);
	}

	/**
	 * Check if this campaign is currently active based on the active flag and date range.
	 * A campaign is active if the active flag is true AND the current time is between start and end dates.
	 */
	public boolean isCurrentlyActive() {
		LocalDateTime now = LocalDateTime.now();
		return active && !now.isBefore(startDate) && !now.isAfter(endDate);
	}
}
