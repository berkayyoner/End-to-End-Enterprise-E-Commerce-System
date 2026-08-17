package com.berkay.product_service.qna.controller;

import com.berkay.product_service.qna.dto.QnaAnswerRequest;
import com.berkay.product_service.qna.dto.QnaQuestionRequest;
import com.berkay.product_service.qna.dto.QnaQuestionResponse;
import com.berkay.product_service.qna.service.QnaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for product Q&A.
 * POST /questions requires authentication (any buyer, no purchase requirement per RULES.md).
 * POST /questions/{id}/answer requires authentication and seller ownership verification.
 * GET /questions is public.
 */
@RestController
@RequestMapping("/products")
public class QnaController {

	private final QnaService qnaService;

	public QnaController(QnaService qnaService) {
		this.qnaService = qnaService;
	}

	/**
	 * Ask a question about a product.
	 * Requires authentication (any buyer, no purchase requirement).
	 */
	@PostMapping("/{productId}/questions")
	public ResponseEntity<QnaQuestionResponse> askQuestion(
			@PathVariable Long productId,
			Authentication authentication,
			@Valid @RequestBody QnaQuestionRequest request) {
		String askerId = authentication.getName();
		QnaQuestionResponse question = qnaService.askQuestion(productId, askerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(question);
	}

	/**
	 * Answer a question about a product.
	 * Requires authentication and must be the product's seller.
	 */
	@PostMapping("/{productId}/questions/{questionId}/answer")
	public ResponseEntity<QnaQuestionResponse> answerQuestion(
			@PathVariable Long productId,
			@PathVariable Long questionId,
			Authentication authentication,
			@Valid @RequestBody QnaAnswerRequest request) {
		String answererId = authentication.getName();
		QnaQuestionResponse question = qnaService.answerQuestion(questionId, answererId, request);
		return ResponseEntity.ok(question);
	}

	/**
	 * Get all questions for a product.
	 * Public endpoint (no authentication required).
	 */
	@GetMapping("/{productId}/questions")
	public ResponseEntity<List<QnaQuestionResponse>> getProductQuestions(
			@PathVariable Long productId) {
		List<QnaQuestionResponse> questions = qnaService.getProductQuestions(productId);
		return ResponseEntity.ok(questions);
	}
}
