package com.berkay.product_service.category.changerequest.service;

import com.berkay.product_service.category.changerequest.dto.CategoryChangeRequestInput;
import com.berkay.product_service.category.changerequest.dto.CategoryChangeRequestResponse;
import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequest;
import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequestStatus;
import com.berkay.product_service.category.changerequest.entity.CategoryLevel;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestAlreadyReviewedException;
import com.berkay.product_service.category.changerequest.exception.CategoryChangeRequestNotFoundException;
import com.berkay.product_service.category.changerequest.repository.CategoryChangeRequestRepository;
import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.SubTypeRequest;
import com.berkay.product_service.category.dto.InnerTypeRequest;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.SubTypeRepository;
import com.berkay.product_service.category.repository.InnerTypeRepository;
import com.berkay.product_service.category.service.MainCategoryService;
import com.berkay.product_service.category.service.SubTypeService;
import com.berkay.product_service.category.service.InnerTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for the category change-request workflow. Moderators submit proposed changes
 * (create or edit), and P0 admins approve (applies the change) or reject (no effect).
 */
@Service
public class CategoryChangeRequestService {

	private final CategoryChangeRequestRepository changeRequestRepository;
	private final MainCategoryService mainCategoryService;
	private final SubTypeService subTypeService;
	private final InnerTypeService innerTypeService;
	private final SubTypeRepository subTypeRepository;
	private final InnerTypeRepository innerTypeRepository;

	public CategoryChangeRequestService(CategoryChangeRequestRepository changeRequestRepository,
			MainCategoryService mainCategoryService, SubTypeService subTypeService,
			InnerTypeService innerTypeService, SubTypeRepository subTypeRepository,
			InnerTypeRepository innerTypeRepository) {
		this.changeRequestRepository = changeRequestRepository;
		this.mainCategoryService = mainCategoryService;
		this.subTypeService = subTypeService;
		this.innerTypeService = innerTypeService;
		this.subTypeRepository = subTypeRepository;
		this.innerTypeRepository = innerTypeRepository;
	}

	@Transactional
	public CategoryChangeRequestResponse submit(CategoryChangeRequestInput input) {
		CategoryChangeRequest changeRequest = new CategoryChangeRequest(
				input.categoryLevel(),
				input.targetEntityId(),
				input.nameTranslationTr(),
				input.nameTranslationEn(),
				input.descriptionTranslationTr(),
				input.descriptionTranslationEn()
		);
		CategoryChangeRequest saved = changeRequestRepository.save(changeRequest);
		return CategoryChangeRequestResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public Page<CategoryChangeRequestResponse> listPending(Pageable pageable) {
		return changeRequestRepository.findAllByStatus(CategoryChangeRequestStatus.PENDING, pageable)
				.map(CategoryChangeRequestResponse::from);
	}

	@Transactional
	public CategoryChangeRequestResponse approve(Long changeRequestId, String reviewerEmail) {
		CategoryChangeRequest changeRequest = findPendingOrThrow(changeRequestId);

		// Apply the proposed change to the real category entity
		List<TranslationInput> translations = List.of(
				new TranslationInput("tr", changeRequest.getNameTranslationTr(),
						changeRequest.getDescriptionTranslationTr()),
				new TranslationInput("en", changeRequest.getNameTranslationEn(),
						changeRequest.getDescriptionTranslationEn())
		);

		if (changeRequest.getCategoryLevel() == CategoryLevel.MAIN) {
			if (changeRequest.getTargetEntityId() == null) {
				// Create new MainCategory
				mainCategoryService.create(new MainCategoryRequest(translations));
			} else {
				// Update existing MainCategory
				mainCategoryService.update(changeRequest.getTargetEntityId(),
						new MainCategoryRequest(translations));
			}
		} else if (changeRequest.getCategoryLevel() == CategoryLevel.SUB) {
			if (changeRequest.getTargetEntityId() == null) {
				// Creating a new SubType is not supported via change requests (no parent ID provided)
				throw new IllegalArgumentException(
						"Creating a new SubType via change request is not supported. Provide a targetEntityId to update an existing SubType.");
			} else {
				// Update existing SubType - load it to get the parent ID
				SubType subType = subTypeRepository.findActiveById(changeRequest.getTargetEntityId())
						.orElseThrow(() -> new CategoryNotFoundException(
								"SubType not found with id: " + changeRequest.getTargetEntityId()));
				Long mainCategoryId = subType.getMainCategory().getId();
				subTypeService.update(changeRequest.getTargetEntityId(),
						new SubTypeRequest(mainCategoryId, translations));
			}
		} else if (changeRequest.getCategoryLevel() == CategoryLevel.INNER) {
			if (changeRequest.getTargetEntityId() == null) {
				// Creating a new InnerType is not supported via change requests (no parent ID provided)
				throw new IllegalArgumentException(
						"Creating a new InnerType via change request is not supported. Provide a targetEntityId to update an existing InnerType.");
			} else {
				// Update existing InnerType - load it to get the parent ID
				InnerType innerType = innerTypeRepository.findActiveById(changeRequest.getTargetEntityId())
						.orElseThrow(() -> new CategoryNotFoundException(
								"InnerType not found with id: " + changeRequest.getTargetEntityId()));
				Long subTypeId = innerType.getSubType().getId();
				innerTypeService.update(changeRequest.getTargetEntityId(),
						new InnerTypeRequest(subTypeId, translations));
			}
		}

		changeRequest.approve(reviewerEmail);
		CategoryChangeRequest saved = changeRequestRepository.save(changeRequest);
		return CategoryChangeRequestResponse.from(saved);
	}

	@Transactional
	public CategoryChangeRequestResponse reject(Long changeRequestId, String reviewerEmail, String reason) {
		CategoryChangeRequest changeRequest = findPendingOrThrow(changeRequestId);
		changeRequest.reject(reviewerEmail, reason);
		CategoryChangeRequest saved = changeRequestRepository.save(changeRequest);
		return CategoryChangeRequestResponse.from(saved);
	}

	private CategoryChangeRequest findPendingOrThrow(Long changeRequestId) {
		CategoryChangeRequest changeRequest = changeRequestRepository.findById(changeRequestId)
				.orElseThrow(() -> new CategoryChangeRequestNotFoundException(
						"Category change request not found with id: " + changeRequestId));
		if (!changeRequest.isPending()) {
			throw new CategoryChangeRequestAlreadyReviewedException(changeRequestId);
		}
		return changeRequest;
	}
}
