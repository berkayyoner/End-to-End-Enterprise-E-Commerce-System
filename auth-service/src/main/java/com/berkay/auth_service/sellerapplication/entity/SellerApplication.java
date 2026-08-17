package com.berkay.auth_service.sellerapplication.entity;

import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * RULES.md's dummy "apply to become a Seller" flow: whatever company info the customer types in
 * is accepted as-is, no real company registry is checked. A personnel with P2 edit access
 * (PERMISSIONS.md) approves or rejects it by hand; approval is what flips AppUser#seller.
 */
@Entity
@Table(name = "seller_application")
public class SellerApplication extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_user_id", nullable = false)
	private AppUser appUser;

	@Column(name = "company_name", nullable = false, length = 200)
	private String companyName;

	@Column(name = "tax_id", nullable = false, length = 50)
	private String taxId;

	@Column(name = "company_phone", nullable = false, length = 32)
	private String companyPhone;

	@Column(name = "company_address", nullable = false, length = 500)
	private String companyAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private SellerApplicationStatus status = SellerApplicationStatus.PENDING;

	@Column(name = "reviewed_by_email", length = 254)
	private String reviewedByEmail;

	@Column(name = "reviewed_at")
	private Instant reviewedAt;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

	protected SellerApplication() {
	}

	public SellerApplication(AppUser appUser, String companyName, String taxId, String companyPhone,
			String companyAddress) {
		this.appUser = appUser;
		this.companyName = companyName;
		this.taxId = taxId;
		this.companyPhone = companyPhone;
		this.companyAddress = companyAddress;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getTaxId() {
		return taxId;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public SellerApplicationStatus getStatus() {
		return status;
	}

	public String getReviewedByEmail() {
		return reviewedByEmail;
	}

	public Instant getReviewedAt() {
		return reviewedAt;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public boolean isPending() {
		return status == SellerApplicationStatus.PENDING;
	}

	public void approve(String reviewerEmail) {
		this.status = SellerApplicationStatus.APPROVED;
		this.reviewedByEmail = reviewerEmail;
		this.reviewedAt = Instant.now();
	}

	public void reject(String reviewerEmail, String reason) {
		this.status = SellerApplicationStatus.REJECTED;
		this.reviewedByEmail = reviewerEmail;
		this.reviewedAt = Instant.now();
		this.rejectionReason = reason;
	}
}
