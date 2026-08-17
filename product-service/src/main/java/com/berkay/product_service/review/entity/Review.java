package com.berkay.product_service.review.entity;

import com.berkay.common.entity.BaseEntity;
import com.berkay.product_service.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A customer review of a product. One review per buyer per product.
 * The reviewer must have a PAID order containing this product (verified at submission time).
 */
@Entity
@Table(name = "review",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_review_product_reviewer",
					columnNames = {"product_id", "reviewer_id"})
		})
public class Review extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_review_product"))
	private Product product;

	@Column(name = "reviewer_id", nullable = false, updatable = false, length = 100)
	private String reviewerId;

	@Column(name = "rating", nullable = false)
	private int rating;  // 1-5

	@Column(name = "comment", length = 1000)
	private String comment;

	protected Review() {
	}

	public Review(Product product, String reviewerId, int rating, String comment) {
		this.product = product;
		this.reviewerId = reviewerId;
		this.rating = rating;
		this.comment = comment;
	}

	public Product getProduct() {
		return product;
	}

	public String getReviewerId() {
		return reviewerId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
