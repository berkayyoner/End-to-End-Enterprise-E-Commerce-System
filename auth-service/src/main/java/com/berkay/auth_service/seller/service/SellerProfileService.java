package com.berkay.auth_service.seller.service;

import com.berkay.auth_service.exception.AppUserNotFoundException;
import com.berkay.auth_service.seller.dto.SellerProfileResponse;
import com.berkay.auth_service.seller.dto.SellerPublicProfileResponse;
import com.berkay.auth_service.seller.dto.UpdateStoreNameRequest;
import com.berkay.auth_service.seller.entity.SellerEarnings;
import com.berkay.auth_service.seller.repository.SellerEarningsRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Seller profile management - exposed via task 4.1 endpoints:
 * - GET /sellers/me (authenticated seller)
 * - PUT /sellers/me/store-name (authenticated seller)
 * - GET /sellers/{id}/public-profile (public)
 *
 * SellerEarnings is a separate entity housing the running earnings total,
 * ready for Phase 5 order-service to call credit() on order completion.
 */
@Service
public class SellerProfileService {

	private final AppUserRepository appUserRepository;
	private final SellerEarningsRepository sellerEarningsRepository;

	public SellerProfileService(AppUserRepository appUserRepository,
			SellerEarningsRepository sellerEarningsRepository) {
		this.appUserRepository = appUserRepository;
		this.sellerEarningsRepository = sellerEarningsRepository;
	}

	/**
	 * Get the authenticated seller's profile (store name, earnings, follower count).
	 * Automatically creates a SellerEarnings record if it doesn't exist yet.
	 */
	@Transactional
	public SellerProfileResponse getSellerProfile(Long sellerId) {
		AppUser seller = appUserRepository.findById(sellerId)
				.orElseThrow(() -> new AppUserNotFoundException(sellerId));

		if (!seller.isSeller()) {
			throw new IllegalStateException("User is not a seller");
		}

		SellerEarnings earnings = sellerEarningsRepository.findBySellerId(sellerId)
				.orElseGet(() -> {
					SellerEarnings newEarnings = new SellerEarnings(sellerId);
					return sellerEarningsRepository.save(newEarnings);
				});

		return SellerProfileResponse.from(seller, earnings.getTotalEarned());
	}

	/**
	 * Update the seller's store name.
	 */
	@Transactional
	public SellerProfileResponse updateStoreName(Long sellerId, UpdateStoreNameRequest request) {
		AppUser seller = appUserRepository.findById(sellerId)
				.orElseThrow(() -> new AppUserNotFoundException(sellerId));

		if (!seller.isSeller()) {
			throw new IllegalStateException("User is not a seller");
		}

		seller.setStoreName(request.storeName().trim());
		appUserRepository.save(seller);

		BigDecimal totalEarned = sellerEarningsRepository.findBySellerId(sellerId)
				.map(SellerEarnings::getTotalEarned)
				.orElse(BigDecimal.ZERO);

		return SellerProfileResponse.from(seller, totalEarned);
	}

	/**
	 * Get public profile for a seller by ID (store name and follower count only, no earnings).
	 */
	@Transactional(readOnly = true)
	public SellerPublicProfileResponse getPublicProfile(Long sellerId) {
		AppUser seller = appUserRepository.findById(sellerId)
				.orElseThrow(() -> new AppUserNotFoundException(sellerId));

		if (!seller.isSeller()) {
			throw new IllegalStateException("User is not a seller");
		}

		return SellerPublicProfileResponse.from(seller);
	}
}
