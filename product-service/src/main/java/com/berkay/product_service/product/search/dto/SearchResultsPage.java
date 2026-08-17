package com.berkay.product_service.product.search.dto;

import com.berkay.product_service.product.search.document.ProductDocument;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Response envelope for search results with pagination metadata for infinite scroll.
 */
public record SearchResultsPage(
		List<ProductSearchResponse> items,
		long totalCount,
		int currentPage,
		boolean hasNextPage
) {
	public static SearchResultsPage from(Page<ProductDocument> page) {
		List<ProductSearchResponse> items = page.getContent().stream()
				.map(ProductSearchResponse::from)
				.toList();

		return new SearchResultsPage(
				items,
				page.getTotalElements(),
				page.getNumber(),
				page.hasNext()
		);
	}
}
