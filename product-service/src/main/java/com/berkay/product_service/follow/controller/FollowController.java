package com.berkay.product_service.follow.controller;

import com.berkay.product_service.follow.dto.FollowResponse;
import com.berkay.product_service.follow.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for seller follows.
 * Most endpoints require authentication (authenticated buyer).
 */
@RestController
@RequestMapping("/sellers")
public class FollowController {

	private final FollowService followService;

	public FollowController(FollowService followService) {
		this.followService = followService;
	}

	/**
	 * Follow a seller.
	 * Requires authentication.
	 */
	@PostMapping("/{sellerId}/follow")
	public ResponseEntity<Void> followSeller(
			@PathVariable String sellerId,
			Authentication authentication) {
		String buyerId = authentication.getName();
		followService.followSeller(sellerId, buyerId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Unfollow a seller.
	 * Requires authentication.
	 */
	@DeleteMapping("/{sellerId}/follow")
	public ResponseEntity<Void> unfollowSeller(
			@PathVariable String sellerId,
			Authentication authentication) {
		String buyerId = authentication.getName();
		followService.unfollowSeller(sellerId, buyerId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Check if authenticated buyer is following a seller.
	 * Requires authentication.
	 *
	 * @param sellerId the seller to check
	 * @return FollowResponse with isFollowing boolean
	 */
	@GetMapping("/{sellerId}/is-following")
	public ResponseEntity<FollowResponse> isFollowing(
			@PathVariable String sellerId,
			Authentication authentication) {
		String buyerId = authentication.getName();
		boolean isFollowing = followService.isFollowing(sellerId, buyerId);
		return ResponseEntity.ok(new FollowResponse(isFollowing));
	}

	/**
	 * Get follower count for a seller.
	 * Public endpoint, no authentication required.
	 * Used by auth-service to fetch real follower counts for seller profiles.
	 *
	 * @param sellerId the seller
	 * @return count of followers
	 */
	@GetMapping("/{sellerId}/follower-count")
	public ResponseEntity<Long> getFollowerCount(@PathVariable String sellerId) {
		long count = followService.getFollowerCount(sellerId);
		return ResponseEntity.ok(count);
	}
}
