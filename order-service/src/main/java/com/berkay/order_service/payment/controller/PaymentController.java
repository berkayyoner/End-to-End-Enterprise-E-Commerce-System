package com.berkay.order_service.payment.controller;

import com.berkay.order_service.payment.dto.SavedCardResponse;
import com.berkay.order_service.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@GetMapping("/saved-card")
	public ResponseEntity<?> getSavedCard(Authentication authentication) {
		String buyerId = authentication.getName();
		Optional<SavedCardResponse> savedCard = paymentService.getMySavedCard(buyerId);
		return savedCard.isPresent()
			? ResponseEntity.ok(savedCard.get())
			: ResponseEntity.noContent().build();
	}
}
