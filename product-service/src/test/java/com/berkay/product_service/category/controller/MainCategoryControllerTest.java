package com.berkay.product_service.category.controller;

import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.service.MainCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainCategoryController.class)
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
