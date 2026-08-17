package com.berkay.auth_service.seller.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client for calling product-service's seller follower count endpoint.
 * Best-effort: any error results in 0 being returned, not a propagated exception,
 * since follower count is informational, not access-gating.
 */
@Component
public class SellerFollowClient {

	private static final Logger logger = LoggerFactory.getLogger(SellerFollowClient.class);

	private final RestClient restClient;
	private final String productServiceUrl;

	public SellerFollowClient(
			@Value("${product-service.url:http://localhost:8083}") String productServiceUrl) {
		this.restClient = RestClient.builder().build();
		this.productServiceUrl = productServiceUrl;
	}

	/**
	 * Get the follower count for a seller from product-service.
	 * Best-effort: any error returns 0 rather than failing the profile fetch.
	 *
	 * @param sellerId the seller's user ID
	 * @return the count of followers, or 0 if product-service is unreachable
	 */
	public long getFollowerCount(String sellerId) {
		try {
			String url = productServiceUrl + "/sellers/" + sellerId + "/follower-count";

			Long count = restClient
					.get()
					.uri(url)
					.retrieve()
					.body(Long.class);

			return count != null ? count : 0L;
		} catch (Exception e) {
			logger.warn("Failed to fetch follower count for seller {} from product-service: {}",
					sellerId, e.getMessage());
			return 0L;  // Best-effort: fallback to 0
		}
	}
}
