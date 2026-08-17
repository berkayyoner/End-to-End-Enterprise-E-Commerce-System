package com.berkay.product_service.category.changerequest.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * RULES.md's category change-request workflow: a moderator submits a proposed change to a
 * category (create new or edit existing at any level: MAIN/SUB/INNER), and a P0 admin must
 * approve or reject it before it takes effect. On approval, the proposed translations are
 * applied to the real MainCategory/SubType/InnerType + their translation tables. On rejection,
 * the request status is just flipped to REJECTED with no side effects.
 */
@Entity
@Table(name = "category_change_request")
public class CategoryChangeRequest extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "category_level", nullable = false, length = 10)
	private CategoryLevel categoryLevel;

	@Column(name = "target_entity_id", nullable = true)
	private Long targetEntityId;

	@Column(name = "name_translation_tr", nullable = false, length = 255)
	private String nameTranslationTr;

	@Column(name = "name_translation_en", nullable = false, length = 255)
	private String nameTranslationEn;

	@Column(name = "description_translation_tr", length = 1000)
	private String descriptionTranslationTr;

	@Column(name = "description_translation_en", length = 1000)
	private String descriptionTranslationEn;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private CategoryChangeRequestStatus status = CategoryChangeRequestStatus.PENDING;

	@Column(name = "reviewed_by_email", length = 254)
	private String reviewedByEmail;

	@Column(name = "reviewed_at")
	private Instant reviewedAt;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

	protected CategoryChangeRequest() {
	}

	public CategoryChangeRequest(CategoryLevel categoryLevel, Long targetEntityId,
			String nameTranslationTr, String nameTranslationEn,
			String descriptionTranslationTr, String descriptionTranslationEn) {
		this.categoryLevel = categoryLevel;
		this.targetEntityId = targetEntityId;
		this.nameTranslationTr = nameTranslationTr;
		this.nameTranslationEn = nameTranslationEn;
		this.descriptionTranslationTr = descriptionTranslationTr;
		this.descriptionTranslationEn = descriptionTranslationEn;
	}

	public CategoryLevel getCategoryLevel() {
		return categoryLevel;
	}

	public Long getTargetEntityId() {
		return targetEntityId;
	}

	public String getNameTranslationTr() {
		return nameTranslationTr;
	}

	public String getNameTranslationEn() {
		return nameTranslationEn;
	}

	public String getDescriptionTranslationTr() {
		return descriptionTranslationTr;
	}

	public String getDescriptionTranslationEn() {
		return descriptionTranslationEn;
	}

	public CategoryChangeRequestStatus getStatus() {
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
		return status == CategoryChangeRequestStatus.PENDING;
	}

	public void approve(String reviewerEmail) {
		this.status = CategoryChangeRequestStatus.APPROVED;
		this.reviewedByEmail = reviewerEmail;
		this.reviewedAt = Instant.now();
	}

	public void reject(String reviewerEmail, String reason) {
		this.status = CategoryChangeRequestStatus.REJECTED;
		this.reviewedByEmail = reviewerEmail;
		this.reviewedAt = Instant.now();
		this.rejectionReason = reason;
	}
}
