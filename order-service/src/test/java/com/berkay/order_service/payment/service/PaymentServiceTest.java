package com.berkay.order_service.payment.service;

import com.berkay.order_service.payment.dto.PaymentRequest;
import com.berkay.order_service.payment.entity.SavedCard;
import com.berkay.order_service.payment.repository.SavedCardRepository;
import com.berkay.order_service.payment.util.AesEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

	@Mock
	private SavedCardRepository savedCardRepository;

	@Mock
	private AesEncryptor aesEncryptor;

	@InjectMocks
	private PaymentService paymentService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSaveCardEncryptsCardNumber() {
		String buyerId = "buyer1";
		PaymentRequest request = new PaymentRequest(
			"4532015112830366",
			12,
			2025,
			"123",
			"John Doe",
			true
		);

		String encryptedCardNumber = "encryptedValue123";
		when(aesEncryptor.encrypt(request.cardNumber())).thenReturn(encryptedCardNumber);

		paymentService.saveCard(buyerId, request);

		ArgumentCaptor<SavedCard> captor = ArgumentCaptor.forClass(SavedCard.class);
		verify(savedCardRepository).save(captor.capture());

		SavedCard savedCard = captor.getValue();
		assertEquals(buyerId, savedCard.getBuyerId());
		assertEquals(encryptedCardNumber, savedCard.getCardNumberEncrypted());
		assertEquals(12, savedCard.getExpiryMonth());
		assertEquals(2025, savedCard.getExpiryYear());
		assertEquals("John Doe", savedCard.getCardHolderName());
	}

	@Test
	void testProcessPaymentValidatesCardData() {
		String buyerId = "buyer1";
		PaymentRequest invalidRequest = new PaymentRequest(
			"123", // Too short
			12,
			2025,
			"123",
			"John Doe",
			false
		);

		assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(buyerId, invalidRequest));
	}

	@Test
	void testProcessPaymentValidatesExpiry() {
		String buyerId = "buyer1";
		PaymentRequest invalidRequest = new PaymentRequest(
			"4532015112830366",
			13, // Invalid month
			2025,
			"123",
			"John Doe",
			false
		);

		assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(buyerId, invalidRequest));
	}

	@Test
	void testProcessPaymentValidatesCVV() {
		String buyerId = "buyer1";
		PaymentRequest invalidRequest = new PaymentRequest(
			"4532015112830366",
			12,
			2025,
			"12", // Too short
			"John Doe",
			false
		);

		assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(buyerId, invalidRequest));
	}

	@Test
	void testProcessPaymentWithoutSavingCard() {
		String buyerId = "buyer1";
		PaymentRequest request = new PaymentRequest(
			"4532015112830366",
			12,
			2025,
			"123",
			"John Doe",
			false
		);

		paymentService.processPayment(buyerId, request);

		verify(savedCardRepository, never()).save(any());
	}
}
