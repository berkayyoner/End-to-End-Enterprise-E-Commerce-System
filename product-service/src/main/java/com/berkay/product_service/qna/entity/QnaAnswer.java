package com.berkay.product_service.qna.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * An answer to a QnaQuestion, provided by the product's seller.
 */
@Entity
@Table(name = "qna_answer")
public class QnaAnswer extends BaseEntity {

	@OneToOne(optional = false)
	@JoinColumn(name = "question_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_qna_answer_question"))
	private QnaQuestion question;

	@Column(name = "answerer_id", nullable = false, updatable = false, length = 100)
	private String answererId;

	@Column(name = "answer_text", nullable = false, length = 1000)
	private String answerText;

	protected QnaAnswer() {
	}

	public QnaAnswer(QnaQuestion question, String answererId, String answerText) {
		this.question = question;
		this.answererId = answererId;
		this.answerText = answerText;
	}

	public QnaQuestion getQuestion() {
		return question;
	}

	public void setQuestion(QnaQuestion question) {
		this.question = question;
	}

	public String getAnswererId() {
		return answererId;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}
