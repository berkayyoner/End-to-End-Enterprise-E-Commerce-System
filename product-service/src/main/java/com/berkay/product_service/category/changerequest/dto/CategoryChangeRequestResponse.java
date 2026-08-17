package com.berkay.product_service.category.changerequest.dto;

import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequest;
import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequestStatus;
import com.berkay.product_service.category.changerequest.entity.CategoryLevel;

import java.time.Instant;

public record CategoryChangeRequestResponse(
		Long id,
		CategoryLevel categoryLevel,
		Long targetEntityId,
		String nameTranslationTr,
		String nameTranslationEn,
		String descriptionTranslationTr,
		String descriptionTranslationEn,
		CategoryChangeRequestStatus status,
		String reviewedByEmail,
		Instant reviewedAt,
		String rejectionReason,
		String submittedByEmail,
		Instant createdAt) {

	public static CategoryChangeRequestResponse from(CategoryChangeRequest request) {
		return new CategoryChangeRequestResponse(
				request.getId(),
				request.getCategoryLevel(),
				request.getTargetEntityId(),
				request.getNameTranslationTr(),
				request.getNameTranslationEn(),
				request.getDescriptionTranslationTr(),
				request.getDescriptionTranslationEn(),
				request.getStatus(),
				request.getReviewedByEmail(),
				request.getReviewedAt(),
				request.getRejectionReason(),
				request.getCreatedBy(),
				request.getCreatedAt()
		);
	}
}
