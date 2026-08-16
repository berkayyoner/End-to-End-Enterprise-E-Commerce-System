package com.berkay.product_service.category.service;

import com.berkay.product_service.category.dto.InnerTypeRequest;
import com.berkay.product_service.category.dto.InnerTypeResponse;
import com.berkay.product_service.category.dto.TranslationInput;
import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.InnerTypeTranslation;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.InnerTypeRepository;
import com.berkay.product_service.category.repository.SubTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for InnerType CRUD operations.
 */
@Service
public class InnerTypeService {

	private final InnerTypeRepository innerTypeRepository;
	private final SubTypeRepository subTypeRepository;

	public InnerTypeService(InnerTypeRepository innerTypeRepository, SubTypeRepository subTypeRepository) {
		this.innerTypeRepository = innerTypeRepository;
		this.subTypeRepository = subTypeRepository;
	}

	@Transactional(readOnly = true)
	public List<InnerTypeResponse> list(String locale) {
		return innerTypeRepository.findAllActive().stream()
				.map(it -> InnerTypeResponse.from(it, locale))
				.toList();
	}

	@Transactional(readOnly = true)
	public InnerTypeResponse get(Long id, String locale) {
		InnerType innerType = findActiveOrThrow(id);
		return InnerTypeResponse.from(innerType, locale);
	}

	@Transactional(readOnly = true)
	public List<InnerTypeResponse> listBySubType(Long subTypeId, String locale) {
		return innerTypeRepository.findActiveBySubTypeId(subTypeId).stream()
				.map(it -> InnerTypeResponse.from(it, locale))
				.toList();
	}

	@Transactional
	public InnerTypeResponse create(InnerTypeRequest request) {
		SubType subType = subTypeRepository.findActiveById(request.subTypeId())
				.orElseThrow(() -> new CategoryNotFoundException("SubType not found with id: " + request.subTypeId()));

		InnerType innerType = new InnerType(subType, "PLACEHOLDER");
		applyTranslations(innerType, request.translations());
		InnerType saved = innerTypeRepository.save(innerType);
		return InnerTypeResponse.from(saved, "tr");
	}

	@Transactional
	public InnerTypeResponse update(Long id, InnerTypeRequest request) {
		InnerType innerType = findActiveOrThrow(id);
		applyTranslations(innerType, request.translations());
		InnerType saved = innerTypeRepository.save(innerType);
		return InnerTypeResponse.from(saved, "tr");
	}

	@Transactional
	public void delete(Long id) {
		InnerType innerType = findActiveOrThrow(id);
		innerType.softDelete();
		innerTypeRepository.save(innerType);
	}

	private void applyTranslations(InnerType innerType, List<TranslationInput> translations) {
		innerType.getTranslations().forEach(t -> t.softDelete());
		for (TranslationInput input : translations) {
			InnerTypeTranslation translation = new InnerTypeTranslation(
					innerType, input.localeCode(), input.name(), input.description());
			innerType.addTranslation(translation);
		}
	}

	private InnerType findActiveOrThrow(Long id) {
		return innerTypeRepository.findActiveById(id)
				.orElseThrow(() -> new CategoryNotFoundException("InnerType not found with id: " + id));
	}
}
