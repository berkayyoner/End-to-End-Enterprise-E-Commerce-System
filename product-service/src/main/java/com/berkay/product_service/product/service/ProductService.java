package com.berkay.product_service.product.service;

import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.exception.CategoryNotFoundException;
import com.berkay.product_service.category.repository.InnerTypeRepository;
import com.berkay.product_service.product.dto.ProductRequest;
import com.berkay.product_service.product.dto.ProductResponse;
import com.berkay.product_service.product.dto.ProductTranslationInput;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.entity.ProductKeyFeature;
import com.berkay.product_service.product.entity.ProductPhoto;
import com.berkay.product_service.product.entity.ProductTranslation;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.exception.ProductOwnershipException;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.product.search.document.ProductDocument;
import com.berkay.product_service.product.search.service.ProductSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

/**
 * Service layer for Product CRUD operations.
 * Syncs Elasticsearch index on create/update/delete with best-effort error handling
 * (search-index write failures do not block product operations).
 */
@Service
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository productRepository;
	private final InnerTypeRepository innerTypeRepository;
	private final ProductSearchService searchService;

	public ProductService(ProductRepository productRepository, InnerTypeRepository innerTypeRepository,
			ProductSearchService searchService) {
		this.productRepository = productRepository;
		this.innerTypeRepository = innerTypeRepository;
		this.searchService = searchService;
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> list(String locale) {
		return productRepository.findAllActive().stream()
				.map(p -> ProductResponse.from(p, locale))
				.toList();
	}

	@Transactional(readOnly = true)
	public ProductResponse get(Long id, String locale) {
		Product product = findActiveOrThrow(id);
		return ProductResponse.from(product, locale);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> listBySeller(Long sellerId, String locale) {
		return productRepository.findActiveBySellerId(sellerId).stream()
				.map(p -> ProductResponse.from(p, locale))
				.toList();
	}

	@Transactional
	public ProductResponse create(Long sellerId, ProductRequest request) {
		InnerType innerType = findInnerTypeOrThrow(request.innerTypeId());

		Product product = new Product(sellerId, innerType, request.price(), request.stock());
		applyTranslations(product, request.translations());
		applyKeyFeatures(product, request.keyFeatures());
		applyPhotos(product, request.photos());

		Product saved = productRepository.save(product);

		// Index in Elasticsearch (best-effort, don't fail the product creation if indexing fails)
		try {
			ProductDocument document = buildProductDocument(saved);
			searchService.indexProduct(document);
		} catch (Exception e) {
			logger.warn("Failed to index product {} in Elasticsearch: {}", saved.getId(), e.getMessage(), e);
		}

		return ProductResponse.from(saved, "tr");
	}

	@Transactional
	public ProductResponse update(Long id, Long sellerId, ProductRequest request) {
		Product product = findActiveOrThrow(id);
		enforceOwnership(product, sellerId);

		InnerType innerType = findInnerTypeOrThrow(request.innerTypeId());
		product.setPrice(request.price());
		product.setStock(request.stock());
		applyTranslations(product, request.translations());
		applyKeyFeatures(product, request.keyFeatures());
		applyPhotos(product, request.photos());

		Product saved = productRepository.save(product);

		// Update in Elasticsearch (best-effort, don't fail the product update if indexing fails)
		try {
			ProductDocument document = buildProductDocument(saved);
			searchService.indexProduct(document);
		} catch (Exception e) {
			logger.warn("Failed to update product {} in Elasticsearch: {}", saved.getId(), e.getMessage(), e);
		}

		return ProductResponse.from(saved, "tr");
	}

	@Transactional
	public void delete(Long id, Long sellerId) {
		Product product = findActiveOrThrow(id);
		enforceOwnership(product, sellerId);
		product.softDelete();
		productRepository.save(product);

		// Remove from Elasticsearch (best-effort, don't fail the product deletion if indexing fails)
		try {
			searchService.deleteProduct(id);
		} catch (Exception e) {
			logger.warn("Failed to delete product {} from Elasticsearch: {}", id, e.getMessage(), e);
		}
	}

	private void applyTranslations(Product product, List<ProductTranslationInput> translations) {
		product.getTranslations().forEach(t -> t.softDelete());
		for (ProductTranslationInput input : translations) {
			ProductTranslation translation = new ProductTranslation(
					product, input.localeCode(), input.name(),
					input.shortDescription(), input.longDescription());
			product.addTranslation(translation);
		}
	}

	private void applyKeyFeatures(Product product, List<String> features) {
		product.clearKeyFeatures();
		if (features != null) {
			for (String feature : features) {
				if (feature != null && !feature.isBlank()) {
					ProductKeyFeature keyFeature = new ProductKeyFeature(product, feature);
					product.addKeyFeature(keyFeature);
				}
			}
		}
	}

	private void applyPhotos(Product product, List<com.berkay.product_service.product.dto.ProductPhotoUpload> photos) {
		product.getPhotos().forEach(p -> p.softDelete());
		if (photos != null && !photos.isEmpty()) {
			if (photos.size() > 10) {
				throw new IllegalArgumentException("Cannot add more than 10 photos to a product");
			}
			for (var photoUpload : photos) {
				byte[] imageData = Base64.getDecoder().decode(photoUpload.imageDataBase64());
				if (imageData.length > 5 * 1024 * 1024) {  // 5 MB limit
					throw new IllegalArgumentException("Photo size cannot exceed 5 MB");
				}
				ProductPhoto photo = new ProductPhoto(product, imageData, photoUpload.displayOrder());
				product.addPhoto(photo);
			}
		}
	}

	private Product findActiveOrThrow(Long id) {
		return productRepository.findActiveById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
	}

	private InnerType findInnerTypeOrThrow(Long id) {
		return innerTypeRepository.findActiveById(id)
				.orElseThrow(() -> new CategoryNotFoundException("InnerType not found with id: " + id));
	}

	private void enforceOwnership(Product product, Long sellerId) {
		if (!product.getSellerId().equals(sellerId)) {
			throw new ProductOwnershipException("You can only modify your own products");
		}
	}

	/**
	 * Build a ProductDocument from a Product entity.
	 * Walks the category hierarchy (InnerType -> SubType -> MainCategory)
	 * to extract all category IDs needed for filtering.
	 */
	private ProductDocument buildProductDocument(Product product) {
		InnerType innerType = product.getInnerType();
		SubType subType = innerType.getSubType();
		Long mainCategoryId = subType.getMainCategory().getId();

		String nameTr = null;
		String nameEn = null;
		for (ProductTranslation trans : product.getTranslations()) {
			if ("tr".equals(trans.getLocaleCode())) {
				nameTr = trans.getName();
			} else if ("en".equals(trans.getLocaleCode())) {
				nameEn = trans.getName();
			}
		}

		return new ProductDocument(
				product.getId(),
				product.getSellerId(),
				innerType.getId(),
				subType.getId(),
				mainCategoryId,
				product.getPrice(),
				product.getStock(),
				nameEn != null ? nameEn : "",
				nameTr != null ? nameTr : "",
				product.getCreatedAt()
		);
	}
}
