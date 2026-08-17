package com.berkay.product_service.review.client;

import com.berkay.product_service.review.exception.PurchaseVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client for calling order-service's purchase verification endpoint.
 * Fails closed: any error results in an exception, preventing review submission.
 */
@Component
public class OrderServiceClient {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceClient.class);

	private final RestClient restClient;
	private final String orderServiceUrl;

	public OrderServiceClient(
			@Value("${order-service.url:http://localhost:8084}") String orderServiceUrl) {
		this.restClient = RestClient.builder().build();
		this.orderServiceUrl = orderServiceUrl;
	}

	/**
	 * Verify that a buyer has a PAID order containing a specific product.
	 * Fails closed: returns false only if the buyer provably has no such order.
	 * Any connectivity/server error throws PurchaseVerificationException.
	 *
	 * @param buyerId the buyer's user ID from JWT sub
	 * @param productId the product ID to verify
	 * @return true if the buyer has a PAID order with this product
	 * @throws PurchaseVerificationException if verification cannot be completed
	 */
	public boolean hasPurchased(String buyerId, Long productId) {
		try {
			String url = orderServiceUrl + "/orders/has-purchased?productId=" + productId;

			PurchaseVerificationResponse response = restClient
					.get()
					.uri(url)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new PurchaseVerificationException(
								"Order service returned " + res.getStatusCode() +
										": " + new String(res.getBody().readAllBytes())
						);
					})
					.body(PurchaseVerificationResponse.class);

			return response != null && response.hasPurchased();
		} catch (PurchaseVerificationException e) {
			// Re-throw our own exceptions as-is
			throw e;
		} catch (Exception e) {
			// Any other error (connectivity, parse, etc.) is a verification failure
			logger.error("Purchase verification failed for buyer {} product {}: {}",
					buyerId, productId, e.getMessage(), e);
			throw new PurchaseVerificationException(
					"Failed to verify purchase with order-service: " + e.getMessage(), e
			);
		}
	}

	/**
	 * DTO matching order-service's PurchaseVerificationResponse.
	 */
	private record PurchaseVerificationResponse(boolean hasPurchased) {
	}
}
