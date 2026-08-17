package com.berkay.product_service.qna.dto;

import com.berkay.product_service.qna.entity.QnaQuestion;
import java.time.Instant;

/**
 * Response containing a Q&A question with optional answer.
 */
public record QnaQuestionResponse(
		Long id,
		String askerId,
		String questionText,
		Instant createdAt,
		QnaAnswerDTO answer
) {
	public static QnaQuestionResponse from(QnaQuestion question) {
		return new QnaQuestionResponse(
				question.getId(),
				question.getAskerId(),
				question.getQuestionText(),
				question.getCreatedAt(),
				question.getAnswer() != null ? QnaAnswerDTO.from(question.getAnswer()) : null
		);
	}
}
