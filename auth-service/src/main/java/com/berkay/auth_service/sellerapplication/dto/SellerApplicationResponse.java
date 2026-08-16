package com.berkay.auth_service.sellerapplication.dto;

import com.berkay.auth_service.sellerapplication.entity.SellerApplication;
import com.berkay.auth_service.sellerapplication.entity.SellerApplicationStatus;

import java.time.Instant;

public record SellerApplicationResponse(
		Long id,
		Long appUserId,
		String appUserEmail,
		String companyName,
		String taxId,
		String companyPhone,
		String companyAddress,
		SellerApplicationStatus status,
		String reviewedByEmail,
		Instant reviewedAt,
		String rejectionReason,
		Instant createdAt) {

	public static SellerApplicationResponse from(SellerApplication application) {
		return new SellerApplicationResponse(
				application.getId(),
				application.getAppUser().getId(),
				application.getAppUser().getEmail(),
				application.getCompanyName(),
				application.getTaxId(),
				application.getCompanyPhone(),
				application.getCompanyAddress(),
				application.getStatus(),
				application.getReviewedByEmail(),
				application.getReviewedAt(),
				application.getRejectionReason(),
				application.getCreatedAt());
	}
}
