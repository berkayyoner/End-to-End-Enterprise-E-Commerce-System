package com.berkay.product_service.category.service;

import com.berkay.product_service.category.dto.MainCategoryRequest;
import com.berkay.product_service.category.dto.MainCategoryResponse;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.MainCategoryTranslation;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.MainCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for MainCategory CRUD operations.
 */
@Service
public class MainCategoryService {

	private final MainCategoryRepository mainCategoryRepository;

	public MainCategoryService(MainCategoryRepository mainCategoryRepository) {
		this.mainCategoryRepository = mainCategoryRepository;
	}

	@Transactional(readOnly = true)
	public List<MainCategoryResponse> list(String locale) {
		return mainCategoryRepository.findAllActive().stream()
				.map(mc -> MainCategoryResponse.from(mc, locale))
				.toList();
	}

	@Transactional(readOnly = true)
	public MainCategoryResponse get(Long id, String locale) {
		MainCategory category = findActiveOrThrow(id);
		return MainCategoryResponse.from(category, locale);
	}

	@Transactional
	public MainCategoryResponse create(MainCategoryRequest request) {
		MainCategory category = new MainCategory("PLACEHOLDER");
		applyTranslations(category, request.translations());
		MainCategory saved = mainCategoryRepository.save(category);
		return MainCategoryResponse.from(saved, "tr");
	}

	@Transactional
	public MainCategoryResponse update(Long id, MainCategoryRequest request) {
		MainCategory category = findActiveOrThrow(id);
		applyTranslations(category, request.translations());
		MainCategory saved = mainCategoryRepository.save(category);
		return MainCategoryResponse.from(saved, "tr");
	}

	@Transactional
	public void delete(Long id) {
		MainCategory category = findActiveOrThrow(id);
		category.softDelete();
		mainCategoryRepository.save(category);
	}

	private void applyTranslations(MainCategory category, List<TranslationInput> translations) {
		category.getTranslations().forEach(t -> t.softDelete());
		for (TranslationInput input : translations) {
			MainCategoryTranslation translation = new MainCategoryTranslation(
					category, input.localeCode(), input.name(), input.description());
			category.addTranslation(translation);
		}
	}

	private MainCategory findActiveOrThrow(Long id) {
		return mainCategoryRepository.findActiveById(id)
				.orElseThrow(() -> new CategoryNotFoundException("MainCategory not found with id: " + id));
	}
}
