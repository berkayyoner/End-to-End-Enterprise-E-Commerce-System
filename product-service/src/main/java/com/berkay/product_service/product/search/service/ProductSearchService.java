package com.berkay.product_service.product.search.service;

import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.entity.ProductTranslation;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.product.search.document.ProductDocument;
import com.berkay.product_service.product.search.repository.ProductSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching products with keyword, filtering, and 7 sort options.
 * Implements search using JPA queries (acceptable substitute per SCOPE PIVOT guidance when
 * Elasticsearch is more trouble than it's worth). Page size is fixed at 20 per RULES.md.
 *
 * The ProductSearchRepository is a placeholder for future Elasticsearch integration.
 */
@Service
public class ProductSearchService {

	private static final int PAGE_SIZE = 20;

	private final ProductRepository productRepository;
	private final ProductSearchRepository searchRepository;

	public ProductSearchService(ProductRepository productRepository,
			ProductSearchRepository searchRepository) {
		this.productRepository = productRepository;
		this.searchRepository = searchRepository;
	}

	/**
	 * Search products with keyword, filters, and sort options.
	 *
	 * @param query             keyword search term (optional)
	 * @param mainCategoryId    filter by main category (optional)
	 * @param subTypeId         filter by sub type (optional)
	 * @param innerTypeId       filter by inner type (optional)
	 * @param minPrice          minimum price filter (optional)
	 * @param maxPrice          maximum price filter (optional)
	 * @param sortOption        one of the 7 RULES.md sort options (required)
	 * @param page              0-indexed page number (default 0)
	 * @return page of products with metadata for infinite scroll
	 */
	public Page<ProductDocument> search(String query, Long mainCategoryId, Long subTypeId,
			Long innerTypeId, BigDecimal minPrice, BigDecimal maxPrice,
			String sortOption, int page) {

		// Fetch all active products (in production, this would be Elasticsearch)
		List<Product> allProducts = productRepository.findAllActive();

		// Filter by keyword (match against tr/en translations)
		List<Product> filtered = allProducts.stream()
				.filter(p -> matchesKeyword(p, query))
				.filter(p -> matchesCategory(p, mainCategoryId, subTypeId, innerTypeId))
				.filter(p -> matchesPriceRange(p, minPrice, maxPrice))
				.collect(Collectors.toList());

		// Convert to ProductDocument for response
		List<ProductDocument> documents = filtered.stream()
				.map(this::toProductDocument)
				.collect(Collectors.toList());

		// Sort
		sortDocuments(documents, sortOption);

		// Paginate
		int totalElements = documents.size();
		int fromIndex = page * PAGE_SIZE;
		int toIndex = Math.min(fromIndex + PAGE_SIZE, totalElements);

		if (fromIndex >= totalElements) {
			return new PageImpl<>(List.of(), PageRequest.of(page, PAGE_SIZE), totalElements);
		}

		List<ProductDocument> pageContent = documents.subList(fromIndex, toIndex);
		return new PageImpl<>(pageContent, PageRequest.of(page, PAGE_SIZE), totalElements);
	}

	/**
	 * Index a product document (placeholder for ES integration).
	 */
	public void indexProduct(ProductDocument document) {
		searchRepository.save(document);
	}

	/**
	 * Remove a product document from index (placeholder for ES integration).
	 */
	public void deleteProduct(Long productId) {
		searchRepository.deleteById(productId);
	}

	private boolean matchesKeyword(Product p, String query) {
		if (query == null || query.isBlank()) {
			return true;
		}
		String lowerQuery = query.toLowerCase();
		return p.getTranslations().stream()
				.anyMatch(t -> t.getName() != null && t.getName().toLowerCase().contains(lowerQuery));
	}

	private boolean matchesCategory(Product p, Long mainCategoryId, Long subTypeId, Long innerTypeId) {
		if (innerTypeId != null) {
			return p.getInnerType().getId().equals(innerTypeId);
		}
		if (subTypeId != null) {
			return p.getInnerType().getSubType().getId().equals(subTypeId);
		}
		if (mainCategoryId != null) {
			return p.getInnerType().getSubType().getMainCategory().getId().equals(mainCategoryId);
		}
		return true;
	}

	private boolean matchesPriceRange(Product p, BigDecimal minPrice, BigDecimal maxPrice) {
		if (minPrice != null && p.getPrice().compareTo(minPrice) < 0) {
			return false;
		}
		if (maxPrice != null && p.getPrice().compareTo(maxPrice) > 0) {
			return false;
		}
		return true;
	}

	private void sortDocuments(List<ProductDocument> documents, String sortOption) {
		Comparator<ProductDocument> comparator = buildComparator(sortOption);
		documents.sort(comparator);
	}

	private Comparator<ProductDocument> buildComparator(String sortOption) {
		if (sortOption == null || sortOption.isBlank()) {
			sortOption = "Suggested Ranking";
		}

		return switch (sortOption) {
			case "The Most Expensive" ->
					Comparator.comparing(ProductDocument::getPrice, Comparator.reverseOrder());
			case "The Cheapest" ->
					Comparator.comparing(ProductDocument::getPrice);
			case "Newest" ->
					Comparator.comparing(ProductDocument::getCreatedAt, Comparator.reverseOrder());
			case "The Most Selling" ->
					Comparator.comparing(ProductDocument::getSalesCount, Comparator.reverseOrder());
			case "The Most Favorited" ->
					Comparator.comparing(ProductDocument::getFavoriteCount, Comparator.reverseOrder());
			case "The Most Rated" ->
					Comparator.comparing(ProductDocument::getAverageRating, Comparator.reverseOrder());
			case "Suggested Ranking" ->
					Comparator.comparing(ProductDocument::getCreatedAt, Comparator.reverseOrder());
			default ->
					Comparator.comparing(ProductDocument::getCreatedAt, Comparator.reverseOrder());
		};
	}

	private ProductDocument toProductDocument(Product product) {
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
				product.getInnerType().getId(),
				product.getInnerType().getSubType().getId(),
				product.getInnerType().getSubType().getMainCategory().getId(),
				product.getPrice(),
				product.getStock(),
				nameEn != null ? nameEn : "",
				nameTr != null ? nameTr : "",
				product.getCreatedAt()
		);
	}
}
