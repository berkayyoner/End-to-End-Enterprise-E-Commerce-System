package com.berkay.auth_service.idverification.entity;

import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * RULES.md's dummy ID verification flow: any ID number is accepted, plus two photos ("2 sides
 * of the ID with client's face"). No real document verification happens - a personnel with P1
 * edit access (see PERMISSIONS.md) approves or rejects it by hand. Photos are stored inline as
 * BLOBs for now; a dedicated object-storage service is a fair future upgrade once real product
 * photo uploads (Phase 3/4) need one at scale, not before.
 */
@Entity
@Table(name = "id_verification_application")
public class IdVerificationApplication extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_user_id", nullable = false)
	private AppUser appUser;

	@Column(name = "id_number", nullable = false, length = 50)
	private String idNumber;

	@Lob
	@Column(name = "front_photo", nullable = false)
	private byte[] frontPhoto;

	@Column(name = "front_photo_content_type", nullable = false, length = 100)
	private String frontPhotoContentType;

	@Lob
	@Column(name = "back_photo", nullable = false)
	private byte[] backPhoto;

	@Column(name = "back_photo_content_type", nullable = false, length = 100)
	private String backPhotoContentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private IdVerificationStatus status = IdVerificationStatus.PENDING;

	@Column(name = "reviewed_by_email", length = 254)
	private String reviewedByEmail;

	@Column(name = "reviewed_at")
	private Instant reviewedAt;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

	protected IdVerificationApplication() {
	}

	public IdVerificationApplication(AppUser appUser, String idNumber, byte[] frontPhoto,
			String frontPhotoContentType, byte[] backPhoto, String backPhotoContentType) {
		this.appUser = appUser;
		this.idNumber = idNumber;
		this.frontPhoto = frontPhoto;
		this.frontPhotoContentType = frontPhotoContentType;
		this.backPhoto = backPhoto;
		this.backPhotoContentType = backPhotoContentType;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public byte[] getFrontPhoto() {
		return frontPhoto;
	}

	public String getFrontPhotoContentType() {
		return frontPhotoContentType;
	}

	public byte[] getBackPhoto() {
		return backPhoto;
	}

	public String getBackPhotoContentType() {
		return backPhotoContentType;
	}

	public IdVerificationStatus getStatus() {
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
		return status == IdVerificationStatus.PENDING;
	}

	public void approve(String reviewerEmail) {
		this.status = IdVerificationStatus.APPROVED;
		this.reviewedByEmail = reviewerEmail;
		this.reviewedAt = Instant.now();
	}

	public void reject(String reviewerEmail, String reason) {
		this.status = IdVerificationStatus.REJECTED;
		this.reviewedByEmail = reviewerEmail;
		this.reviewedAt = Instant.now();
		this.rejectionReason = reason;
	}
}
