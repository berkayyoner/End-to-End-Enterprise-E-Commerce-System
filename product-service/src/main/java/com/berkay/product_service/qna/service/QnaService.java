package com.berkay.product_service.qna.service;

import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.qna.dto.QnaAnswerRequest;
import com.berkay.product_service.qna.dto.QnaQuestionRequest;
import com.berkay.product_service.qna.dto.QnaQuestionResponse;
import com.berkay.product_service.qna.entity.QnaAnswer;
import com.berkay.product_service.qna.entity.QnaQuestion;
import com.berkay.product_service.qna.exception.QuestionNotFoundException;
import com.berkay.product_service.qna.exception.SellerOwnershipException;
import com.berkay.product_service.qna.repository.QnaQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing product Q&A.
 * Questions can be asked by any authenticated buyer (no purchase requirement per RULES.md).
 * Answers can only be provided by the product's seller.
 */
@Service
@Transactional
public class QnaService {

	private final QnaQuestionRepository qnaQuestionRepository;
	private final ProductRepository productRepository;

	public QnaService(
			QnaQuestionRepository qnaQuestionRepository,
			ProductRepository productRepository) {
		this.qnaQuestionRepository = qnaQuestionRepository;
		this.productRepository = productRepository;
	}

	/**
	 * Ask a question about a product.
	 * No purchase requirement per RULES.md - any authenticated buyer can ask.
	 *
	 * @param productId the product being asked about
	 * @param askerId the buyer's user ID (from JWT sub)
	 * @param request the question text
	 * @return the persisted question
	 * @throws ProductNotFoundException if product doesn't exist
	 */
	public QnaQuestionResponse askQuestion(Long productId, String askerId, QnaQuestionRequest request) {
		// Verify product exists
		Product product = productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

		// Create and persist question
		QnaQuestion question = new QnaQuestion(product, askerId, request.questionText());
		QnaQuestion saved = qnaQuestionRepository.save(question);

		return QnaQuestionResponse.from(saved);
	}

	/**
	 * Answer a question about a product.
	 * Only the product's seller can answer questions about their products.
	 *
	 * @param questionId the question being answered
	 * @param answererId the seller's user ID (from JWT sub, must own the product)
	 * @param request the answer text
	 * @return the updated question with answer
	 * @throws QuestionNotFoundException if question doesn't exist
	 * @throws SellerOwnershipException if caller is not the product's owner
	 */
	public QnaQuestionResponse answerQuestion(Long questionId, String answererId, QnaAnswerRequest request) {
		// Find the question
		QnaQuestion question = qnaQuestionRepository.findById(questionId)
				.orElseThrow(() -> new QuestionNotFoundException("Question not found: " + questionId));

		// Verify the caller owns the product
		if (!question.getProduct().getSellerId().equals(Long.parseLong(answererId))) {
			throw new SellerOwnershipException(
					"Only the product owner can answer questions about this product"
			);
		}

		// Create and attach the answer
		QnaAnswer answer = new QnaAnswer(question, answererId, request.answerText());
		question.setAnswer(answer);
		QnaQuestion updated = qnaQuestionRepository.save(question);

		return QnaQuestionResponse.from(updated);
	}

	/**
	 * Get all questions for a product (excludes deleted questions).
	 */
	@Transactional(readOnly = true)
	public List<QnaQuestionResponse> getProductQuestions(Long productId) {
		// Verify product exists
		productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

		return qnaQuestionRepository.findByProductIdAndDeletedFalse(productId).stream()
				.map(QnaQuestionResponse::from)
				.toList();
	}
}
