package com.berkay.product_service.product.search.controller;

import com.berkay.product_service.product.search.dto.SearchResultsPage;
import com.berkay.product_service.product.search.service.ProductSearchService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.product_service.product.search.document.ProductDocument;

import java.math.BigDecimal;

/**
 * REST controller for product search via Elasticsearch.
 * Public endpoint (no authentication required) supporting keyword search,
 * category/price filtering, and 7 sort options with infinite scroll pagination.
 */
@RestController
@RequestMapping("/products/search")
public class ProductSearchController {

	private final ProductSearchService searchService;

	public ProductSearchController(ProductSearchService searchService) {
		this.searchService = searchService;
	}

	/**
	 * Search products with keyword, filters, and sort options.
	 *
	 * @param q                keyword search (optional)
	 * @param mainCategoryId   filter by main category (optional)
	 * @param subTypeId        filter by sub type (optional)
	 * @param innerTypeId      filter by inner type (optional)
	 * @param minPrice         minimum price (optional)
	 * @param maxPrice         maximum price (optional)
	 * @param sort             sort option: "Suggested Ranking", "The Most Expensive",
	 *                         "The Cheapest", "Newest", "The Most Selling",
	 *                         "The Most Favorited", or "The Most Rated" (default: "Suggested Ranking")
	 * @param page             0-indexed page number (default: 0)
	 * @return page of products with pagination metadata
	 */
	@GetMapping
	public SearchResultsPage search(
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "mainCategoryId", required = false) Long mainCategoryId,
			@RequestParam(value = "subTypeId", required = false) Long subTypeId,
			@RequestParam(value = "innerTypeId", required = false) Long innerTypeId,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
			@RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
			@RequestParam(value = "sort", defaultValue = "Suggested Ranking") String sort,
			@RequestParam(value = "page", defaultValue = "0") int page) {

		Page<ProductDocument> results = searchService.search(q, mainCategoryId, subTypeId,
				innerTypeId, minPrice, maxPrice, sort, page);

		return SearchResultsPage.from(results);
	}
}
