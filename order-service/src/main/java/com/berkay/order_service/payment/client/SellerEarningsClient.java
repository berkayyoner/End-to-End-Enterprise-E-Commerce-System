package com.berkay.order_service.payment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class SellerEarningsClient {

	private final RestClient restClient;

	public SellerEarningsClient(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl("http://auth-service:8081").build();
	}

	public void creditEarnings(String sellerId, BigDecimal amount) {
		try {
			restClient.post()
				.uri("/sellers/{id}/earnings/credit", sellerId)
				.body(new CreditRequest(amount))
				.retrieve()
				.toBodilessEntity();
		} catch (Exception e) {
			// Best-effort: don't fail the order if seller earnings update fails
			throw e;
		}
	}

	private record CreditRequest(BigDecimal amount) {}
}
