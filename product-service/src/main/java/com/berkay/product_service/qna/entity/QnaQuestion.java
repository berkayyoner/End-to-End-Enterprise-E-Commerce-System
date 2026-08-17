package com.berkay.product_service.qna.entity;

import com.berkay.common.entity.BaseEntity;
import com.berkay.product_service.product.entity.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * A question asked by a customer about a product.
 * Can have an optional answer from the product's seller.
 */
@Entity
@Table(name = "qna_question")
public class QnaQuestion extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_qna_question_product"))
	private Product product;

	@Column(name = "asker_id", nullable = false, updatable = false, length = 100)
	private String askerId;

	@Column(name = "question_text", nullable = false, length = 1000)
	private String questionText;

	@OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	private QnaAnswer answer;

	protected QnaQuestion() {
	}

	public QnaQuestion(Product product, String askerId, String questionText) {
		this.product = product;
		this.askerId = askerId;
		this.questionText = questionText;
	}

	public Product getProduct() {
		return product;
	}

	public String getAskerId() {
		return askerId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public QnaAnswer getAnswer() {
		return answer;
	}

	public void setAnswer(QnaAnswer answer) {
		this.answer = answer;
		if (answer != null) {
			answer.setQuestion(this);
		}
	}

	public void removeAnswer() {
		if (this.answer != null) {
			this.answer.setQuestion(null);
		}
		this.answer = null;
	}
}
