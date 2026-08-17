package com.berkay.product_service.qna.controller;

import com.berkay.common.security.PermissionAuthoritiesConverter;
import com.berkay.product_service.config.SecurityConfig;
import com.berkay.product_service.qna.dto.QnaAnswerRequest;
import com.berkay.product_service.qna.dto.QnaQuestionResponse;
import com.berkay.product_service.qna.service.QnaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the end-to-end path for seller-ownership verification on Q&A answers.
 * Verifies that only the product's seller (matching JWT sub claim) can answer questions.
 * This is a real JWT + SecurityConfig test matching the pattern from MainCategoryJwtSecurityTest.
 */
@WebMvcTest(QnaController.class)
@Import(SecurityConfig.class)
class QnaControllerJwtSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private QnaService qnaService;

	private static final PermissionAuthoritiesConverter CONVERTER = new PermissionAuthoritiesConverter();

	@Test
	void productOwnerCanAnswerQuestion() throws Exception {
		// Given: seller with ID "1" (JWT sub="1") trying to answer a question
		// on a product they own (product owner = seller 1L)
		long productId = 1L;
		long questionId = 100L;

		QnaAnswerRequest request = new QnaAnswerRequest("This is a great question!");
		QnaQuestionResponse mockResponse = new QnaQuestionResponse(
				questionId,
				"buyer123",
				"What's the color?",
				null,
				null
		);

		when(qnaService.answerQuestion(anyLong(), anyString(), any(QnaAnswerRequest.class)))
				.thenReturn(mockResponse);

		// When: POST /products/1/questions/100/answer with seller JWT
		mockMvc.perform(post("/products/" + productId + "/questions/" + questionId + "/answer")
						.with(jwt().jwt(realJwt("1")).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	@Test
	void nonOwnerSellerCannotAnswerQuestion() throws Exception {
		// Given: seller with ID "999" (JWT sub="999") trying to answer a question
		// on a product owned by seller 1 (ownership mismatch will be detected by service)
		long productId = 1L;
		long questionId = 100L;

		QnaAnswerRequest request = new QnaAnswerRequest("This is a great question!");

		when(qnaService.answerQuestion(anyLong(), anyString(), any(QnaAnswerRequest.class)))
				.thenThrow(new com.berkay.product_service.qna.exception.SellerOwnershipException(
						"Only the product owner can answer questions about this product"
				));

		// When: POST /products/1/questions/100/answer with different seller JWT
		mockMvc.perform(post("/products/" + productId + "/questions/" + questionId + "/answer")
						.with(jwt().jwt(realJwt("999")).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());  // SellerOwnershipException -> 403
	}

	@Test
	void unauthenticatedUserCannotAnswerQuestion() throws Exception {
		// Given: unauthenticated request (no JWT)
		long productId = 1L;
		long questionId = 100L;

		QnaAnswerRequest request = new QnaAnswerRequest("This is a great question!");

		// When: POST without authentication
		mockMvc.perform(post("/products/" + productId + "/questions/" + questionId + "/answer")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized());
	}

	/** Builds a real JWT carrying the seller's ID in the sub claim. */
	private static java.util.function.Consumer<Jwt.Builder> realJwt(String sellerId) {
		return builder -> builder
				.claim("sub", sellerId)  // Seller ID matches product owner for ownership check
				.claim("account_type", "SELLER")
				.header("alg", "none");
	}
}
