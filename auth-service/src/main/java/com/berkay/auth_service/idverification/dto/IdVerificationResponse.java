package com.berkay.auth_service.idverification.dto;

import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.entity.IdVerificationStatus;

import java.time.Instant;
import java.util.Base64;

public record IdVerificationResponse(
		Long id,
		Long appUserId,
		String appUserEmail,
		String idNumber,
		IdVerificationStatus status,
		String reviewedByEmail,
		Instant reviewedAt,
		String rejectionReason,
		Instant createdAt,
		String frontPhotoBase64,
		String frontPhotoContentType,
		String backPhotoBase64,
		String backPhotoContentType) {

	public static IdVerificationResponse from(IdVerificationApplication application) {
		String frontPhotoBase64 = application.getFrontPhoto() != null
				? "data:" + application.getFrontPhotoContentType() + ";base64," + Base64.getEncoder().encodeToString(application.getFrontPhoto())
				: null;
		String backPhotoBase64 = application.getBackPhoto() != null
				? "data:" + application.getBackPhotoContentType() + ";base64," + Base64.getEncoder().encodeToString(application.getBackPhoto())
				: null;

		return new IdVerificationResponse(
				application.getId(),
				application.getAppUser().getId(),
				application.getAppUser().getEmail(),
				application.getIdNumber(),
				application.getStatus(),
				application.getReviewedByEmail(),
				application.getReviewedAt(),
				application.getRejectionReason(),
				application.getCreatedAt(),
				frontPhotoBase64,
				application.getFrontPhotoContentType(),
				backPhotoBase64,
				application.getBackPhotoContentType());
	}
}
