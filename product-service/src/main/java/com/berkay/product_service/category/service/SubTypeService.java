package com.berkay.product_service.category.service;

import com.berkay.product_service.category.dto.SubTypeRequest;
import com.berkay.product_service.category.dto.SubTypeResponse;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.entity.SubTypeTranslation;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.MainCategoryRepository;
import com.berkay.product_service.category.repository.SubTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for SubType CRUD operations.
 */
@Service
public class SubTypeService {

	private final SubTypeRepository subTypeRepository;
	private final MainCategoryRepository mainCategoryRepository;

	public SubTypeService(SubTypeRepository subTypeRepository, MainCategoryRepository mainCategoryRepository) {
		this.subTypeRepository = subTypeRepository;
		this.mainCategoryRepository = mainCategoryRepository;
	}

	@Transactional(readOnly = true)
	public List<SubTypeResponse> list(String locale) {
		return subTypeRepository.findAllActive().stream()
				.map(st -> SubTypeResponse.from(st, locale))
				.toList();
	}

	@Transactional(readOnly = true)
	public SubTypeResponse get(Long id, String locale) {
		SubType subType = findActiveOrThrow(id);
		return SubTypeResponse.from(subType, locale);
	}

	@Transactional(readOnly = true)
	public List<SubTypeResponse> listByMainCategory(Long mainCategoryId, String locale) {
		return subTypeRepository.findActiveByMainCategoryId(mainCategoryId).stream()
				.map(st -> SubTypeResponse.from(st, locale))
				.toList();
	}

	@Transactional
	public SubTypeResponse create(SubTypeRequest request) {
		MainCategory mainCategory = mainCategoryRepository.findActiveById(request.mainCategoryId())
				.orElseThrow(() -> new CategoryNotFoundException("MainCategory not found with id: " + request.mainCategoryId()));

		SubType subType = new SubType(mainCategory, "PLACEHOLDER");
		applyTranslations(subType, request.translations());
		SubType saved = subTypeRepository.save(subType);
		return SubTypeResponse.from(saved, "tr");
	}

	@Transactional
	public SubTypeResponse update(Long id, SubTypeRequest request) {
		SubType subType = findActiveOrThrow(id);
		applyTranslations(subType, request.translations());
		SubType saved = subTypeRepository.save(subType);
		return SubTypeResponse.from(saved, "tr");
	}

	@Transactional
	public void delete(Long id) {
		SubType subType = findActiveOrThrow(id);
		subType.softDelete();
		subTypeRepository.save(subType);
	}

	private void applyTranslations(SubType subType, List<TranslationInput> translations) {
		subType.getTranslations().forEach(t -> t.softDelete());
		for (TranslationInput input : translations) {
			SubTypeTranslation translation = new SubTypeTranslation(
					subType, input.localeCode(), input.name(), input.description());
			subType.addTranslation(translation);
		}
	}

	private SubType findActiveOrThrow(Long id) {
		return subTypeRepository.findActiveById(id)
				.orElseThrow(() -> new CategoryNotFoundException("SubType not found with id: " + id));
	}
}
