package com.berkay.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BasketNotFoundException.class)
	public ResponseEntity<Object> handleBasketNotFound(BasketNotFoundException ex) {
		Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<Object> handleOrderNotFound(OrderNotFoundException ex) {
		Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OrderOwnershipException.class)
	public ResponseEntity<Object> handleOrderOwnershipException(OrderOwnershipException ex) {
		Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(InvalidCouponException.class)
	public ResponseEntity<Object> handleInvalidCoupon(InvalidCouponException ex) {
		Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("errors", ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.collect(Collectors.toMap(
				error -> error.getField(),
				error -> error.getDefaultMessage(),
				(msg1, msg2) -> msg1 + "; " + msg2
			)));
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	private Map<String, Object> buildErrorResponse(String message, HttpStatus status) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", message);
		return body;
	}
}