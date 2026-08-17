package com.berkay.order_service.payment.service;

import com.berkay.order_service.payment.dto.PaymentRequest;
import com.berkay.order_service.payment.dto.SavedCardResponse;
import com.berkay.order_service.payment.entity.SavedCard;
import com.berkay.order_service.payment.repository.SavedCardRepository;
import com.berkay.order_service.payment.util.AesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PaymentService {

	private final SavedCardRepository savedCardRepository;
	private final AesEncryptor aesEncryptor;

	public PaymentService(SavedCardRepository savedCardRepository, AesEncryptor aesEncryptor) {
		this.savedCardRepository = savedCardRepository;
		this.aesEncryptor = aesEncryptor;
	}

	public void processPayment(String buyerId, PaymentRequest request) {
		// Validate card data (basic shape validation only, not real gateway check)
		validateCardData(request);

		// Save card if requested
		if (request.saveCard() != null && request.saveCard()) {
			saveCard(buyerId, request);
		}

		// In a real system, call the payment gateway here
		// For now, just accept any card as dummy payment
	}

	public void saveCard(String buyerId, PaymentRequest request) {
		String encryptedCardNumber = aesEncryptor.encrypt(request.cardNumber());

		SavedCard savedCard = new SavedCard(
			buyerId,
			encryptedCardNumber,
			request.expiryMonth(),
			request.expiryYear(),
			request.cardHolderName()
		);

		savedCardRepository.save(savedCard);
	}

	public Optional<SavedCardResponse> getMySavedCard(String buyerId) {
		return savedCardRepository.findByBuyerIdAndDeletedFalse(buyerId)
			.map(this::mapToResponse);
	}

	private SavedCardResponse mapToResponse(SavedCard savedCard) {
		String decryptedCardNumber = aesEncryptor.decrypt(savedCard.getCardNumberEncrypted());
		String last4 = decryptedCardNumber.substring(decryptedCardNumber.length() - 4);

		return new SavedCardResponse(
			last4,
			savedCard.getExpiryMonth(),
			savedCard.getExpiryYear(),
			savedCard.getCardHolderName()
		);
	}

	private void validateCardData(PaymentRequest request) {
		// Validate card number length
		if (request.cardNumber() == null || request.cardNumber().length() < 13 || request.cardNumber().length() > 19) {
			throw new IllegalArgumentException("Invalid card number");
		}

		// Validate expiry
		if (request.expiryMonth() == null || request.expiryMonth() < 1 || request.expiryMonth() > 12) {
			throw new IllegalArgumentException("Invalid expiry month");
		}

		if (request.expiryYear() == null || request.expiryYear() < 2024) {
			throw new IllegalArgumentException("Invalid expiry year");
		}

		// Validate CVV
		if (request.cvv() == null || (request.cvv().length() < 3 || request.cvv().length() > 4)) {
			throw new IllegalArgumentException("Invalid CVV");
		}
	}
}
