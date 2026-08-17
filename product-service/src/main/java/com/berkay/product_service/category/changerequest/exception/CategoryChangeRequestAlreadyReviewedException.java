package com.berkay.product_service.category.changerequest.exception;

public class CategoryChangeRequestAlreadyReviewedException extends RuntimeException {
	public CategoryChangeRequestAlreadyReviewedException(Long id) {
		super("Category change request " + id + " has already been reviewed");
	}
}
