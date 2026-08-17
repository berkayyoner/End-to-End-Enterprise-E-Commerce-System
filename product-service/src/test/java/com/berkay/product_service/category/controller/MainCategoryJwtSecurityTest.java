package com.berkay.product_service.category.controller;

import com.berkay.common.security.PermissionAuthoritiesConverter;
import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.MainCategoryResponse;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.service.MainCategoryService;
import com.berkay.product_service.config.SecurityConfig;
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
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the actual end-to-end path a real bearer token takes: a JWT carrying the compact
 * "permissions" claim from auth-service -> PermissionAuthoritiesConverter (real instance, not
 * mocked) -> decomposed Spring Security authorities -> the controller's @PreAuthorize check.
 * This is distinct from {@link MainCategoryControllerTest}, which uses @WithMockUser to inject
 * an authority string directly and never exercises the claim-to-authority conversion at all -
 * that gap is exactly how the PERM_P4AED vs PERM_P4_ADD mismatch bug went undetected.
 */
@WebMvcTest(MainCategoryController.class)
@Import(SecurityConfig.class)
class MainCategoryJwtSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MainCategoryService mainCategoryService;

	private static final PermissionAuthoritiesConverter CONVERTER = new PermissionAuthoritiesConverter();

	@Test
	void jwtWithFullP4PermissionCanCreateCategory() throws Exception {
		when(mainCategoryService.create(any())).thenReturn(new MainCategoryResponse(1L, "Elektronikler", null));

		MainCategoryRequest request = new MainCategoryRequest(List.of(
				new TranslationInput("tr", "Elektronikler", "Elektronik ürünleri")));

		mockMvc.perform(post("/categories/main")
						.with(jwt().jwt(realJwt(List.of("P4AED"))).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
	}

	@Test
	void jwtWithViewOnlyP4PermissionCannotCreateCategory() throws Exception {
		MainCategoryRequest request = new MainCategoryRequest(List.of(
				new TranslationInput("tr", "Elektronikler", "Elektronik ürünleri")));

		mockMvc.perform(post("/categories/main")
						.with(jwt().jwt(realJwt(List.of("P4"))).authorities(CONVERTER))
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
	}

	@Test
	void publicGetListRequiresNoAuthentication() throws Exception {
		when(mainCategoryService.list("tr")).thenReturn(List.of());

		mockMvc.perform(get("/categories/main"))
				.andExpect(status().isOk());
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
