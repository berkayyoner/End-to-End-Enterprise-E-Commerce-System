package com.berkay.product_service.product.controller;

import com.berkay.common.security.PermissionAuthoritiesConverter;
import com.berkay.product_service.product.dto.ProductRequest;
import com.berkay.product_service.product.dto.ProductResponse;
import com.berkay.product_service.product.dto.ProductTranslationInput;
import com.berkay.product_service.product.service.ProductService;
import com.berkay.product_service.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Personnel Product endpoints with real JWT permission validation.
 * Ensures that P5 permission codes are correctly decomposed and enforced:
 * - P5 alone permits VIEW
 * - P5E (or P5ED/P5AE/etc.) permits EDIT
 * - P5D (or P5ED/P5AED) permits DELETE
 */
@WebMvcTest(PersonnelProductController.class)
@Import(SecurityConfig.class)
class PersonnelProductJwtSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProductService productService;

	private static final PermissionAuthoritiesConverter CONVERTER = new PermissionAuthoritiesConverter();

	@Test
	void viewOnlyP5CanListProducts() throws Exception {
		when(productService.listAllForPersonnel("tr")).thenReturn(List.of());

		mockMvc.perform(get("/personnel/products")
						.with(jwt().jwt(realJwt(List.of("P5"))).authorities(CONVERTER)))
				.andExpect(status().isOk());
	}

	@Test
	void fullP5EDCanListProducts() throws Exception {
		when(productService.listAllForPersonnel("tr")).thenReturn(List.of());

		mockMvc.perform(get("/personnel/products")
						.with(jwt().jwt(realJwt(List.of("P5ED"))).authorities(CONVERTER)))
				.andExpect(status().isOk());
	}

	@Test
	void jwtWithoutP5CannotListProducts() throws Exception {
		mockMvc.perform(get("/personnel/products")
						.with(jwt().jwt(realJwt(List.of("P4"))).authorities(CONVERTER)))
				.andExpect(status().isForbidden());
	}

	@Test
	void jwtWithP5ECanUpdateProduct() throws Exception {
		ProductRequest request = new ProductRequest(
				1L,
				new BigDecimal("99.99"),
				100,
				List.of(new ProductTranslationInput("tr", "Ürün", "Kısa açıklama", "Uzun açıklama")),
				List.of("Özellik 1"),
				null
		);

		when(productService.updateForPersonnel(eq(1L), any(ProductRequest.class)))
				.thenReturn(new ProductResponse(1L, 1L, 1L, new BigDecimal("99.99"), 100, "Ürün", "Kısa", "Uzun", List.of(), List.of()));

		mockMvc.perform(put("/personnel/products/1")
						.with(jwt().jwt(realJwt(List.of("P5E"))).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	@Test
	void jwtWithFullP5EDCanUpdateProduct() throws Exception {
		ProductRequest request = new ProductRequest(
				1L,
				new BigDecimal("99.99"),
				100,
				List.of(new ProductTranslationInput("tr", "Ürün", "Kısa açıklama", "Uzun açıklama")),
				List.of("Özellik 1"),
				null
		);

		when(productService.updateForPersonnel(eq(1L), any(ProductRequest.class)))
				.thenReturn(new ProductResponse(1L, 1L, 1L, new BigDecimal("99.99"), 100, "Ürün", "Kısa", "Uzun", List.of(), List.of()));

		mockMvc.perform(put("/personnel/products/1")
						.with(jwt().jwt(realJwt(List.of("P5ED"))).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	@Test
	void jwtWithViewOnlyP5CannotUpdateProduct() throws Exception {
		ProductRequest request = new ProductRequest(
				1L,
				new BigDecimal("99.99"),
				100,
				List.of(new ProductTranslationInput("tr", "Ürün", "Kısa açıklama", "Uzun açıklama")),
				List.of("Özellik 1"),
				null
		);

		mockMvc.perform(put("/personnel/products/1")
						.with(jwt().jwt(realJwt(List.of("P5"))).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
	}

	@Test
	void jwtWithP5DCanDeleteProduct() throws Exception {
		mockMvc.perform(delete("/personnel/products/1")
						.with(jwt().jwt(realJwt(List.of("P5D"))).authorities(CONVERTER)))
				.andExpect(status().isNoContent());
	}

	@Test
	void jwtWithFullP5EDCanDeleteProduct() throws Exception {
		mockMvc.perform(delete("/personnel/products/1")
						.with(jwt().jwt(realJwt(List.of("P5ED"))).authorities(CONVERTER)))
				.andExpect(status().isNoContent());
	}

	@Test
	void jwtWithViewOnlyP5CannotDeleteProduct() throws Exception {
		mockMvc.perform(delete("/personnel/products/1")
						.with(jwt().jwt(realJwt(List.of("P5"))).authorities(CONVERTER)))
				.andExpect(status().isForbidden());
	}

	@Test
	void jwtWithoutAuthenticationCannotAccessPersonnelEndpoints() throws Exception {
		mockMvc.perform(get("/personnel/products"))
				.andExpect(status().isUnauthorized());
	}

	/** Builds a Jwt carrying the same "permissions" claim shape auth-service issues (OAUTH2.md §3). */
	private static java.util.function.Consumer<Jwt.Builder> realJwt(List<String> permissionCodes) {
		return builder -> builder
				.claim("permissions", permissionCodes)
				.claim("account_type", "PERSONNEL")
				.claim("sub", "1")
				.header("alg", "none");
	}
}
