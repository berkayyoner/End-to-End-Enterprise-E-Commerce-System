package com.berkay.product_service.qna.dto;

import com.berkay.product_service.qna.entity.QnaAnswer;
import java.time.Instant;

/**
 * DTO for a Q&A answer.
 */
public record QnaAnswerDTO(
		Long id,
		String answererId,
		String answerText,
		Instant createdAt
) {
	public static QnaAnswerDTO from(QnaAnswer answer) {
		return new QnaAnswerDTO(
				answer.getId(),
				answer.getAnswererId(),
				answer.getAnswerText(),
				answer.getCreatedAt()
		);
	}
}
