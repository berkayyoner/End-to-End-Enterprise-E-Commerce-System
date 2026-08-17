package com.berkay.product_service.qna.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to ask a question about a product.
 */
public record QnaQuestionRequest(
		@NotBlank
		String questionText
) {
}
