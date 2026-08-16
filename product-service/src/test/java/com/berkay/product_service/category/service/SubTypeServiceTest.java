package com.berkay.product_service.category.service;

import com.berkay.product_service.category.dto.SubTypeRequest;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.MainCategoryRepository;
import com.berkay.product_service.category.repository.SubTypeRepository;
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
class SubTypeServiceTest {

	@Mock
	private SubTypeRepository subTypeRepository;

	@Mock
	private MainCategoryRepository mainCategoryRepository;

	private SubTypeService service;

	@BeforeEach
	void setUp() {
		service = new SubTypeService(subTypeRepository, mainCategoryRepository);
	}

	@Test
	void createsSubTypeUnderMainCategory() {
		MainCategory mainCategory = new MainCategory("PLACEHOLDER");
		SubTypeRequest request = new SubTypeRequest(1L, List.of(
				new TranslationInput("tr", "Telefon", "Cep telefonları"),
				new TranslationInput("en", "Phone", "Mobile phones")
		));

		when(mainCategoryRepository.findActiveById(1L)).thenReturn(Optional.of(mainCategory));
		SubType saved = new SubType(mainCategory, "PLACEHOLDER");
		when(subTypeRepository.save(any(SubType.class))).thenReturn(saved);

		service.create(request);

		verify(subTypeRepository).save(any(SubType.class));
	}

	@Test
	void throwsExceptionWhenMainCategoryNotFound() {
		SubTypeRequest request = new SubTypeRequest(99L, List.of(
				new TranslationInput("tr", "Test", null)
		));

		when(mainCategoryRepository.findActiveById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(CategoryNotFoundException.class)
				.hasMessageContaining("MainCategory not found");
	}

	@Test
	void deletesSubTypeSuccessfully() {
		MainCategory mainCategory = new MainCategory("PLACEHOLDER");
		SubType subType = new SubType(mainCategory, "PLACEHOLDER");
		when(subTypeRepository.findActiveById(1L)).thenReturn(Optional.of(subType));
		when(subTypeRepository.save(any(SubType.class))).thenReturn(subType);

		service.delete(1L);

		verify(subTypeRepository).save(any(SubType.class));
	}
}
