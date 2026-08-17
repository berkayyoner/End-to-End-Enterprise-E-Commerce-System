package com.berkay.product_service.qna.repository;

import com.berkay.product_service.qna.entity.QnaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaQuestionRepository extends JpaRepository<QnaQuestion, Long> {

	/**
	 * Find all non-deleted questions for a product.
	 */
	List<QnaQuestion> findByProductIdAndDeletedFalse(Long productId);
}
