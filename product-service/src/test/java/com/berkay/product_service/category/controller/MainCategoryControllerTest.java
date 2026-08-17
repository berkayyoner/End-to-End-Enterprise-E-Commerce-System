package com.berkay.product_service.category.controller;

import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.service.MainCategoryService;
import com.berkay.product_service.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code @WebMvcTest} does NOT component-scan arbitrary user {@code @Configuration} classes
 * (only {@code @Controller}/{@code @ControllerAdvice}/etc. are auto-included) - without this
 * {@code @Import}, Spring Boot's own auto-configured default resource-server chain
 * ({@code jwtSecurityFilterChain}, requiring authentication for every path with no method
 * security at all) would silently stand in for our real {@link SecurityConfig}, making any
 * assertion here pass or fail for the wrong reason instead of exercising the real
 * {@code @PreAuthorize} + decomposed-permission logic.
 */
@WebMvcTest(MainCategoryController.class)
@Import(SecurityConfig.class)
class MainCategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MainCategoryService mainCategoryService;

	@Test
	@WithMockUser(authorities = "PERM_P3_ADD")
	void rejectCreateWithWrongPermission() throws Exception {
		MainCategoryRequest request = new MainCategoryRequest(List.of(
				new TranslationInput("tr", "Elektronikler", "Elektronik ürünleri")
		));

		mockMvc.perform(post("/categories/main")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
	}
}
