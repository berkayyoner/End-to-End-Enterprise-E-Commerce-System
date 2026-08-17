package com.berkay.product_service.product.entity;

import com.berkay.common.entity.BaseEntity;
import com.berkay.product_service.category.entity.InnerType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Product entity representing a sellable item. References an InnerType category and a seller
 * (stored as a plain ID since the seller is in auth-service, a different service).
 * Extends BaseEntity for soft-delete and audit columns.
 */
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

	@Column(name = "seller_id", nullable = false)
	private Long sellerId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "inner_type_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_product_inner_type"))
	private InnerType innerType;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "stock", nullable = false)
	private int stock;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductTranslation> translations = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductPhoto> photos = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductKeyFeature> keyFeatures = new ArrayList<>();

	protected Product() {
	}

	public Product(Long sellerId, InnerType innerType, BigDecimal price, int stock) {
		this.sellerId = sellerId;
		this.innerType = innerType;
		this.price = price;
		this.stock = stock;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public InnerType getInnerType() {
		return innerType;
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

	public List<ProductTranslation> getTranslations() {
		return List.copyOf(translations);
	}

	public void addTranslation(ProductTranslation translation) {
		translations.add(translation);
	}

	public List<ProductPhoto> getPhotos() {
		return List.copyOf(photos);
	}

	public void addPhoto(ProductPhoto photo) {
		if (photos.size() >= 10) {
			throw new IllegalArgumentException("Cannot add more than 10 photos to a product");
		}
		photos.add(photo);
	}

	public List<ProductKeyFeature> getKeyFeatures() {
		return List.copyOf(keyFeatures);
	}

	public void addKeyFeature(ProductKeyFeature feature) {
		keyFeatures.add(feature);
	}

	public void clearKeyFeatures() {
		keyFeatures.clear();
	}
}
