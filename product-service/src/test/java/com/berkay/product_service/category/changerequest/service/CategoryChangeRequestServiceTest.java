package com.berkay.product_service.category.changerequest.service;

import com.berkay.product_service.category.changerequest.dto.CategoryChangeRequestInput;
import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequest;
import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequestStatus;
import com.berkay.product_service.category.changerequest.entity.CategoryLevel;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestAlreadyReviewedException;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestNotFoundException;
import com.berkay.product_service.category.changerequest.repository.CategoryChangeRequestRepository;
import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.service.MainCategoryService;
import com.berkay.product_service.category.service.SubTypeService;
import com.berkay.product_service.category.service.InnerTypeService;
import com.berkay.product_service.category.repository.SubTypeRepository;
import com.berkay.product_service.category.repository.InnerTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CategoryChangeRequestServiceTest {

	@Mock
	private CategoryChangeRequestRepository changeRequestRepository;

	@Mock
	private MainCategoryService mainCategoryService;

	@Mock
	private SubTypeService subTypeService;

	@Mock
	private InnerTypeService innerTypeService;

	@Mock
	private SubTypeRepository subTypeRepository;

	@Mock
	private InnerTypeRepository innerTypeRepository;

	private CategoryChangeRequestService service;

	@BeforeEach
	void setUp() {
		service = new CategoryChangeRequestService(changeRequestRepository, mainCategoryService,
				subTypeService, innerTypeService, subTypeRepository, innerTypeRepository);
	}

	@Test
	void submitCreatesChangeRequest() {
		CategoryChangeRequestInput input = new CategoryChangeRequestInput(
				CategoryLevel.MAIN,
				null,
				"Elektronikler",
				"Electronics",
				"Elektronik ürünleri",
				"Electronic products"
		);

		CategoryChangeRequest saved = new CategoryChangeRequest(
				CategoryLevel.MAIN,
				null,
				"Elektronikler",
				"Electronics",
				"Elektronik ürünleri",
				"Electronic products"
		);

		when(changeRequestRepository.save(any(CategoryChangeRequest.class))).thenReturn(saved);

		service.submit(input);

		verify(changeRequestRepository).save(any(CategoryChangeRequest.class));
	}

	@Test
	void approveUpdatesExistingMainCategory() {
		CategoryChangeRequest changeRequest = new CategoryChangeRequest(
				CategoryLevel.MAIN,
				1L,
				"Updated Electronics",
				"Updated Electronics",
				"Güncellenmiş ürünler",
				"Updated products"
		);

		when(changeRequestRepository.findById(10L)).thenReturn(Optional.of(changeRequest));
		when(changeRequestRepository.save(any(CategoryChangeRequest.class))).thenReturn(changeRequest);

		service.approve(10L, "admin@example.com");

		// Verify that the existing category service was called with the right data
		ArgumentCaptor<MainCategoryRequest> captor = ArgumentCaptor.forClass(MainCategoryRequest.class);
		verify(mainCategoryService).update(eq(1L), captor.capture());

		MainCategoryRequest request = captor.getValue();
		assertEquals(2, request.translations().size());
		assertEquals("Updated Electronics", request.translations().get(0).name());
	}

	@Test
	void approveCreatesNewMainCategory() {
		CategoryChangeRequest changeRequest = new CategoryChangeRequest(
				CategoryLevel.MAIN,
				null, // targetEntityId is null, so create new
				"New Electronics",
				"New Electronics",
				"Yeni ürünler",
				"New products"
		);

		when(changeRequestRepository.findById(11L)).thenReturn(Optional.of(changeRequest));
		when(changeRequestRepository.save(any(CategoryChangeRequest.class))).thenReturn(changeRequest);

		service.approve(11L, "admin@example.com");

		// Verify that create was called, not update
		ArgumentCaptor<MainCategoryRequest> captor = ArgumentCaptor.forClass(MainCategoryRequest.class);
		verify(mainCategoryService).create(captor.capture());

		MainCategoryRequest request = captor.getValue();
		assertEquals(2, request.translations().size());
	}

	@Test
	void rejectDoesNotApplyChange() {
		CategoryChangeRequest changeRequest = new CategoryChangeRequest(
				CategoryLevel.MAIN,
				1L,
				"Some Name",
				"Some Name",
				"Açıklama",
				"Description"
		);

		when(changeRequestRepository.findById(12L)).thenReturn(Optional.of(changeRequest));
		when(changeRequestRepository.save(any(CategoryChangeRequest.class))).thenReturn(changeRequest);

		service.reject(12L, "admin@example.com", "Invalid proposal");

		// Verify that no category service was called
		verify(mainCategoryService, never()).create(any());
		verify(mainCategoryService, never()).update(any(), any());
		verify(subTypeService, never()).update(any(), any());
		verify(innerTypeService, never()).update(any(), any());

		// Verify that the request was saved
		verify(changeRequestRepository).save(any(CategoryChangeRequest.class));
	}

	@Test
	void approveThrowsExceptionIfNotFound() {
		when(changeRequestRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.approve(999L, "admin@example.com"))
				.isInstanceOf(CategoryChangeRequestNotFoundException.class);
	}

	@Test
	void approveThrowsExceptionIfAlreadyReviewed() {
		CategoryChangeRequest changeRequest = new CategoryChangeRequest(
				CategoryLevel.MAIN,
				null,
				"Name",
				"Name",
				"Desc",
				"Desc"
		);
		changeRequest.approve("admin@example.com");

		when(changeRequestRepository.findById(13L)).thenReturn(Optional.of(changeRequest));

		assertThatThrownBy(() -> service.approve(13L, "admin@example.com"))
				.isInstanceOf(CategoryChangeRequestAlreadyReviewedException.class);
	}
}
