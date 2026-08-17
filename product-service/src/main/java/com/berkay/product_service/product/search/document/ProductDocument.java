package com.berkay.product_service.product.search.document;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ProductSearchDocument represents a denormalized view of Product data for search operations.
 * In the thin-scoped implementation per SCOPE PIVOT, this is stored/indexed via
 * Elasticsearch, but the repository provides a simple interface.
 *
 * Forward-compatible fields (salesCount, favoriteCount, averageRating) are defaulted
 * to 0 for now — they will be populated by future tasks (6.1/6.2) without requiring
 * a schema migration.
 */
public class ProductDocument {

	private Long id;
	private Long sellerId;
	private Long innerTypeId;
	private Long subTypeId;
	private Long mainCategoryId;
	private BigDecimal price;
	private int stock;
	private String nameEn;
	private String nameTr;
	private Instant createdAt;
	private int salesCount = 0;
	private int favoriteCount = 0;
	private BigDecimal averageRating = BigDecimal.ZERO;

	protected ProductDocument() {
	}

	public ProductDocument(Long id, Long sellerId, Long innerTypeId, Long subTypeId,
			Long mainCategoryId, BigDecimal price, int stock,
			String nameEn, String nameTr, Instant createdAt) {
		this.id = id;
		this.sellerId = sellerId;
		this.innerTypeId = innerTypeId;
		this.subTypeId = subTypeId;
		this.mainCategoryId = mainCategoryId;
		this.price = price;
		this.stock = stock;
		this.nameEn = nameEn;
		this.nameTr = nameTr;
		this.createdAt = createdAt;
		this.salesCount = 0;
		this.favoriteCount = 0;
		this.averageRating = BigDecimal.ZERO;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Long getInnerTypeId() {
		return innerTypeId;
	}

	public void setInnerTypeId(Long innerTypeId) {
		this.innerTypeId = innerTypeId;
	}

	public Long getSubTypeId() {
		return subTypeId;
	}

	public void setSubTypeId(Long subTypeId) {
		this.subTypeId = subTypeId;
	}

	public Long getMainCategoryId() {
		return mainCategoryId;
	}

	public void setMainCategoryId(Long mainCategoryId) {
		this.mainCategoryId = mainCategoryId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameTr() {
		return nameTr;
	}

	public void setNameTr(String nameTr) {
		this.nameTr = nameTr;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public int getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(int salesCount) {
		this.salesCount = salesCount;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public BigDecimal getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(BigDecimal averageRating) {
		this.averageRating = averageRating;
	}
}
