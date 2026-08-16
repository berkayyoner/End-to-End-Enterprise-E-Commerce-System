package com.berkay.auth_service.idverification.dto;

import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.entity.IdVerificationStatus;

import java.time.Instant;

public record IdVerificationResponse(
		Long id,
		Long appUserId,
		String appUserEmail,
		String idNumber,
		IdVerificationStatus status,
		String reviewedByEmail,
		Instant reviewedAt,
		String rejectionReason,
		Instant createdAt) {

	public static IdVerificationResponse from(IdVerificationApplication application) {
		return new IdVerificationResponse(
				application.getId(),
				application.getAppUser().getId(),
				application.getAppUser().getEmail(),
				application.getIdNumber(),
				application.getStatus(),
				application.getReviewedByEmail(),
				application.getReviewedAt(),
				application.getRejectionReason(),
				application.getCreatedAt());
	}
}
