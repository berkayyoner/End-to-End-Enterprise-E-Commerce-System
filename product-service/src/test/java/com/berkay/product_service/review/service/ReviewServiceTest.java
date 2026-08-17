package com.berkay.product_service.review.service;

import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.review.client.OrderServiceClient;
import com.berkay.product_service.review.dto.ReviewRequest;
import com.berkay.product_service.review.dto.ReviewResponse;
import com.berkay.product_service.review.entity.Review;
import com.berkay.product_service.review.exception.DuplicateReviewException;
import com.berkay.product_service.review.exception.PurchaseVerificationException;
import com.berkay.product_service.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private OrderServiceClient orderServiceClient;

	private ReviewService service;
	private Product product;

	@BeforeEach
	void setUp() {
		service = new ReviewService(reviewRepository, productRepository, orderServiceClient);

		// Create a test product
		MainCategory mainCategory = new MainCategory("PLACEHOLDER");
		SubType subType = new SubType(mainCategory, "PLACEHOLDER");
		InnerType innerType = new InnerType(subType, "PLACEHOLDER");
		product = new Product(1L, innerType, BigDecimal.valueOf(100), 10);
	}

	@Test
	void submitReviewSuccess() {
		// Given
		String reviewerId = "buyer123";
		ReviewRequest request = new ReviewRequest(5, "Great product!");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(reviewRepository.findByProductIdAndReviewerIdAndDeletedFalse(1L, reviewerId))
				.thenReturn(Optional.empty());
		when(orderServiceClient.hasPurchased(reviewerId, 1L)).thenReturn(true);
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
			Review review = invocation.getArgument(0);
			// Simulate what JPA does - set an ID on save
			return review;
		});

		// When
		ReviewResponse response = service.submitReview(1L, reviewerId, request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.rating()).isEqualTo(5);
		assertThat(response.comment()).isEqualTo("Great product!");
		assertThat(response.reviewerId()).isEqualTo(reviewerId);
		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	void submitReviewDuplicateFails() {
		// Given
		String reviewerId = "buyer123";
		ReviewRequest request = new ReviewRequest(5, "Great product!");
		Review existingReview = new Review(product, reviewerId, 4, "Good");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(reviewRepository.findByProductIdAndReviewerIdAndDeletedFalse(1L, reviewerId))
				.thenReturn(Optional.of(existingReview));

		// When & Then
		assertThatThrownBy(() -> service.submitReview(1L, reviewerId, request))
				.isInstanceOf(DuplicateReviewException.class)
				.hasMessageContaining("already reviewed");
		verify(reviewRepository, never()).save(any());
	}

	@Test
	void submitReviewPurchaseVerificationFails() {
		// Given
		String reviewerId = "buyer123";
		ReviewRequest request = new ReviewRequest(5, "Great product!");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(reviewRepository.findByProductIdAndReviewerIdAndDeletedFalse(1L, reviewerId))
				.thenReturn(Optional.empty());
		when(orderServiceClient.hasPurchased(reviewerId, 1L)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> service.submitReview(1L, reviewerId, request))
				.isInstanceOf(PurchaseVerificationException.class)
				.hasMessageContaining("must have purchased");
		verify(reviewRepository, never()).save(any());
	}

	@Test
	void submitReviewOrderServiceUnreachableFails() {
		// Given
		String reviewerId = "buyer123";
		ReviewRequest request = new ReviewRequest(5, "Great product!");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(reviewRepository.findByProductIdAndReviewerIdAndDeletedFalse(1L, reviewerId))
				.thenReturn(Optional.empty());
		when(orderServiceClient.hasPurchased(reviewerId, 1L))
				.thenThrow(new PurchaseVerificationException("Order service unreachable"));

		// When & Then
		assertThatThrownBy(() -> service.submitReview(1L, reviewerId, request))
				.isInstanceOf(PurchaseVerificationException.class)
				.hasMessageContaining("Order service");
		verify(reviewRepository, never()).save(any());
	}

	@Test
	void submitReviewProductNotFoundFails() {
		// Given
		String reviewerId = "buyer123";
		ReviewRequest request = new ReviewRequest(5, "Great product!");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> service.submitReview(1L, reviewerId, request))
				.isInstanceOf(ProductNotFoundException.class);
		verify(orderServiceClient, never()).hasPurchased(anyString(), anyLong());
		verify(reviewRepository, never()).save(any());
	}

	@Test
	void getProductReviewsSuccess() {
		// Given
		Review review1 = new Review(product, "buyer1", 5, "Great!");
		Review review2 = new Review(product, "buyer2", 4, "Good");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(reviewRepository.findByProductIdAndDeletedFalse(1L))
				.thenReturn(List.of(review1, review2));

		// When
		List<ReviewResponse> reviews = service.getProductReviews(1L);

		// Then
		assertThat(reviews).hasSize(2);
		assertThat(reviews.get(0).rating()).isEqualTo(5);
		assertThat(reviews.get(1).rating()).isEqualTo(4);
	}

	@Test
	void getProductReviewsProductNotFoundFails() {
		// Given
		when(productRepository.findActiveById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> service.getProductReviews(1L))
				.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void getAverageRatingSuccess() {
		// Given
		when(reviewRepository.findAverageRatingByProductIdAndDeletedFalse(1L)).thenReturn(4.5);

		// When
		double rating = service.getAverageRating(1L);

		// Then
		assertThat(rating).isEqualTo(4.5);
	}

	@Test
	void getRatingCountSuccess() {
		// Given
		when(reviewRepository.countByProductIdAndDeletedFalse(1L)).thenReturn(10L);

		// When
		long count = service.getRatingCount(1L);

		// Then
		assertThat(count).isEqualTo(10);
	}
}
