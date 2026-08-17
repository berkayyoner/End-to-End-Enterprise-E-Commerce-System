package com.berkay.product_service.qna.service;

import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.qna.dto.QnaAnswerRequest;
import com.berkay.product_service.qna.dto.QnaQuestionRequest;
import com.berkay.product_service.qna.dto.QnaQuestionResponse;
import com.berkay.product_service.qna.entity.QnaAnswer;
import com.berkay.product_service.qna.entity.QnaQuestion;
import com.berkay.product_service.qna.exception.QuestionNotFoundException;
import com.berkay.product_service.qna.exception.SellerOwnershipException;
import com.berkay.product_service.qna.repository.QnaQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QnaServiceTest {

	@Mock
	private QnaQuestionRepository qnaQuestionRepository;

	@Mock
	private ProductRepository productRepository;

	private QnaService service;
	private Product product;

	@BeforeEach
	void setUp() {
		service = new QnaService(qnaQuestionRepository, productRepository);

		// Create a test product owned by seller 1L
		MainCategory mainCategory = new MainCategory("PLACEHOLDER");
		SubType subType = new SubType(mainCategory, "PLACEHOLDER");
		InnerType innerType = new InnerType(subType, "PLACEHOLDER");
		product = new Product(1L, innerType, BigDecimal.valueOf(100), 10);
	}

	@Test
	void askQuestionSuccess() {
		// Given
		String askerId = "buyer123";
		QnaQuestionRequest request = new QnaQuestionRequest("What's the color?");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(qnaQuestionRepository.save(any(QnaQuestion.class))).thenAnswer(invocation -> {
			QnaQuestion question = invocation.getArgument(0);
			return question;
		});

		// When
		QnaQuestionResponse response = service.askQuestion(1L, askerId, request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.questionText()).isEqualTo("What's the color?");
		assertThat(response.askerId()).isEqualTo(askerId);
		assertThat(response.answer()).isNull();
		verify(qnaQuestionRepository).save(any(QnaQuestion.class));
	}

	@Test
	void askQuestionProductNotFoundFails() {
		// Given
		String askerId = "buyer123";
		QnaQuestionRequest request = new QnaQuestionRequest("What's the color?");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> service.askQuestion(1L, askerId, request))
				.isInstanceOf(ProductNotFoundException.class);
		verify(qnaQuestionRepository, never()).save(any());
	}

	@Test
	void answerQuestionSuccess() {
		// Given
		String sellerId = "1";  // Must match product.getSellerId() = 1L
		QnaQuestion question = new QnaQuestion(product, "buyer123", "What's the color?");
		QnaAnswerRequest request = new QnaAnswerRequest("It's blue");

		when(qnaQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
		when(qnaQuestionRepository.save(any(QnaQuestion.class))).thenAnswer(invocation -> {
			QnaQuestion q = invocation.getArgument(0);
			return q;
		});

		// When
		QnaQuestionResponse response = service.answerQuestion(1L, sellerId, request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.answer()).isNotNull();
		assertThat(response.answer().answerText()).isEqualTo("It's blue");
		assertThat(response.answer().answererId()).isEqualTo(sellerId);
		verify(qnaQuestionRepository).save(any(QnaQuestion.class));
	}

	@Test
	void answerQuestionNonOwnerSellerFails() {
		// Given
		String otherSellerId = "999";  // Does not match product.getSellerId() = 1L
		QnaQuestion question = new QnaQuestion(product, "buyer123", "What's the color?");
		QnaAnswerRequest request = new QnaAnswerRequest("It's blue");

		when(qnaQuestionRepository.findById(1L)).thenReturn(Optional.of(question));

		// When & Then
		assertThatThrownBy(() -> service.answerQuestion(1L, otherSellerId, request))
				.isInstanceOf(SellerOwnershipException.class)
				.hasMessageContaining("Only the product owner");
		verify(qnaQuestionRepository, never()).save(any());
	}

	@Test
	void answerQuestionNotFoundFails() {
		// Given
		String sellerId = "1";
		QnaAnswerRequest request = new QnaAnswerRequest("It's blue");

		when(qnaQuestionRepository.findById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> service.answerQuestion(1L, sellerId, request))
				.isInstanceOf(QuestionNotFoundException.class);
		verify(qnaQuestionRepository, never()).save(any());
	}

	@Test
	void getProductQuestionsSuccess() {
		// Given
		QnaQuestion question1 = new QnaQuestion(product, "buyer1", "What's the color?");
		QnaQuestion question2 = new QnaQuestion(product, "buyer2", "Is it waterproof?");

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(qnaQuestionRepository.findByProductIdAndDeletedFalse(1L))
				.thenReturn(List.of(question1, question2));

		// When
		List<QnaQuestionResponse> questions = service.getProductQuestions(1L);

		// Then
		assertThat(questions).hasSize(2);
		assertThat(questions.get(0).questionText()).isEqualTo("What's the color?");
		assertThat(questions.get(1).questionText()).isEqualTo("Is it waterproof?");
	}

	@Test
	void getProductQuestionsProductNotFoundFails() {
		// Given
		when(productRepository.findActiveById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> service.getProductQuestions(1L))
				.isInstanceOf(ProductNotFoundException.class);
	}
}
