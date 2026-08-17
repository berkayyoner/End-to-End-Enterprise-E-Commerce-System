package com.berkay.product_service.exception;

import com.berkay.product_service.campaign.exception.CampaignNotFoundException;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestNotFoundException;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestAlreadyReviewedException;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.exception.ProductOwnershipException;
import com.berkay.product_service.review.exception.DuplicateReviewException;
import com.berkay.product_service.review.exception.PurchaseVerificationException;
import com.berkay.product_service.qna.exception.QuestionNotFoundException;
import com.berkay.product_service.qna.exception.SellerOwnershipException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				fieldErrors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errorBody(HttpStatus.BAD_REQUEST, fieldErrors));
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(errorBody(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"));
	}

	@ExceptionHandler(CategoryChangeRequestNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCategoryChangeRequestNotFound(CategoryChangeRequestNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(CategoryChangeRequestAlreadyReviewedException.class)
	public ResponseEntity<Map<String, Object>> handleCategoryChangeRequestAlreadyReviewed(CategoryChangeRequestAlreadyReviewedException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(errorBody(HttpStatus.CONFLICT, ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(ProductOwnershipException.class)
	public ResponseEntity<Map<String, Object>> handleProductOwnership(ProductOwnershipException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(errorBody(HttpStatus.FORBIDDEN, ex.getMessage()));
	}

	@ExceptionHandler(DuplicateReviewException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicateReview(DuplicateReviewException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(errorBody(HttpStatus.CONFLICT, ex.getMessage()));
	}

	@ExceptionHandler(PurchaseVerificationException.class)
	public ResponseEntity<Map<String, Object>> handlePurchaseVerification(PurchaseVerificationException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(errorBody(HttpStatus.FORBIDDEN, ex.getMessage()));
	}

	@ExceptionHandler(QuestionNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleQuestionNotFound(QuestionNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(SellerOwnershipException.class)
	public ResponseEntity<Map<String, Object>> handleSellerOwnership(SellerOwnershipException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(errorBody(HttpStatus.FORBIDDEN, ex.getMessage()));
	}

	@ExceptionHandler(CampaignNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCampaignNotFound(CampaignNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	private Map<String, Object> errorBody(HttpStatus status, Object message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return body;
	}
}
