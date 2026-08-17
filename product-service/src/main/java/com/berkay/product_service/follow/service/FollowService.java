package com.berkay.product_service.follow.service;

import com.berkay.product_service.follow.dto.FollowResponse;
import com.berkay.product_service.follow.entity.Follow;
import com.berkay.product_service.follow.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing seller follows.
 * Allows authenticated buyers to follow and unfollow sellers.
 */
@Service
@Transactional
public class FollowService {

	private final FollowRepository followRepository;

	public FollowService(FollowRepository followRepository) {
		this.followRepository = followRepository;
	}

	/**
	 * Follow a seller.
	 * If already following, this is a no-op (idempotent).
	 *
	 * @param sellerId the seller to follow
	 * @param buyerId the buyer's user ID (from JWT sub)
	 */
	public void followSeller(String sellerId, String buyerId) {
		// Check if already following, if not create new follow
		followRepository.findByBuyerIdAndSellerIdAndDeletedFalse(buyerId, sellerId)
				.orElseGet(() -> {
					Follow follow = new Follow(buyerId, sellerId);
					return followRepository.save(follow);
				});
	}

	/**
	 * Unfollow a seller.
	 * If not following, this is a no-op (idempotent).
	 *
	 * @param sellerId the seller to unfollow
	 * @param buyerId the buyer's user ID (from JWT sub)
	 */
	public void unfollowSeller(String sellerId, String buyerId) {
		followRepository.findByBuyerIdAndSellerIdAndDeletedFalse(buyerId, sellerId)
				.ifPresent(follow -> follow.softDelete());
	}

	/**
	 * Check if buyer is following seller.
	 *
	 * @param sellerId the seller
	 * @param buyerId the buyer's user ID (from JWT sub)
	 * @return true if following, false otherwise
	 */
	@Transactional(readOnly = true)
	public boolean isFollowing(String sellerId, String buyerId) {
		return followRepository.findByBuyerIdAndSellerIdAndDeletedFalse(buyerId, sellerId)
				.isPresent();
	}

	/**
	 * Get the count of followers for a seller.
	 *
	 * @param sellerId the seller
	 * @return the count of active follows
	 */
	@Transactional(readOnly = true)
	public long getFollowerCount(String sellerId) {
		return followRepository.countBySellerIdAndDeletedFalse(sellerId);
	}
}
