package com.berkay.product_service.qna.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to answer a product question.
 */
public record QnaAnswerRequest(
		@NotBlank
		String answerText
) {
}
