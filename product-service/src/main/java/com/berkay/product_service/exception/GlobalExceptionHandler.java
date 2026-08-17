package com.berkay.product_service.exception;

import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestNotFoundException;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestAlreadyReviewedException;
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

	private Map<String, Object> errorBody(HttpStatus status, Object message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return body;
	}
}
