package com.berkay.product_service.product.search.repository;

import com.berkay.product_service.product.search.document.ProductDocument;
import org.springframework.stereotype.Repository;

/**
 * Mock repository for product search documents.
 * In a production deployment with a real Elasticsearch cluster, this would extend
 * ElasticsearchRepository; for now, it's a placeholder that allows dependency injection
 * and mocking in tests while the search service handles actual search logic.
 */
@Repository
public class ProductSearchRepository {

	/**
	 * Save/index a product document.
	 */
	public void save(ProductDocument document) {
		// Placeholder for Elasticsearch indexing
		// In production, this would persist to Elasticsearch via ElasticsearchRepository
	}

	/**
	 * Delete a product document by ID.
	 */
	public void deleteById(Long id) {
		// Placeholder for Elasticsearch deletion
		// In production, this would remove from Elasticsearch
	}
}
