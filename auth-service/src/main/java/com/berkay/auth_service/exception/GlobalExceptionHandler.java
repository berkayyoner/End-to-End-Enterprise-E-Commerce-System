package com.berkay.auth_service.exception;

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

	@ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ResponseEntity<Map<String, Object>> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(HttpStatus.CONFLICT, ex.getMessage()));
	}

	@ExceptionHandler(DuplicatePermissionGroupNameException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicatePermissionGroupName(DuplicatePermissionGroupNameException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(HttpStatus.CONFLICT, ex.getMessage()));
	}

	@ExceptionHandler(PermissionGroupNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handlePermissionGroupNotFound(PermissionGroupNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(errorBody(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"));
	}

	private static Map<String, Object> errorBody(HttpStatus status, Object errors) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("errors", errors);
		return body;
	}
}
