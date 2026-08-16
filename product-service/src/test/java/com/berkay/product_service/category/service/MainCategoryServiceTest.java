package com.berkay.product_service.category.service;

import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.MainCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainCategoryServiceTest {

	@Mock
	private MainCategoryRepository mainCategoryRepository;

	private MainCategoryService service;

	@BeforeEach
	void setUp() {
		service = new MainCategoryService(mainCategoryRepository);
	}

	@Test
	void createsMainCategoryWithTranslations() {
		MainCategoryRequest request = new MainCategoryRequest(List.of(
				new TranslationInput("tr", "Elektronikler", "Elektronik ürünleri"),
				new TranslationInput("en", "Electronics", "Electronic products")
		));

		MainCategory saved = new MainCategory("PLACEHOLDER");
		when(mainCategoryRepository.save(any(MainCategory.class))).thenReturn(saved);

		service.create(request);

		verify(mainCategoryRepository).save(any(MainCategory.class));
	}

	@Test
	void throwsExceptionWhenMainCategoryNotFound() {
		when(mainCategoryRepository.findActiveById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.get(99L, "tr"))
				.isInstanceOf(CategoryNotFoundException.class)
				.hasMessageContaining("MainCategory not found");
	}

	@Test
	void softDeletesMainCategory() {
		MainCategory category = new MainCategory("PLACEHOLDER");
		when(mainCategoryRepository.findActiveById(1L)).thenReturn(Optional.of(category));
		when(mainCategoryRepository.save(any(MainCategory.class))).thenReturn(category);

		service.delete(1L);

		verify(mainCategoryRepository).save(any(MainCategory.class));
	}
}
