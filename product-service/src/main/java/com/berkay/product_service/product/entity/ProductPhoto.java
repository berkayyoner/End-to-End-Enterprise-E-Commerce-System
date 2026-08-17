package com.berkay.product_service.product.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A single photo for a Product, stored as a BLOB. Maximum 10 photos per product.
 * displayOrder determines the order in a photo slider (0–9).
 */
@Entity
@Table(name = "product_photo")
public class ProductPhoto extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_product_photo_product"))
	private Product product;

	@Lob
	@Column(name = "image_data", nullable = false)
	private byte[] imageData;

	@Column(name = "display_order", nullable = false)
	private int displayOrder;

	protected ProductPhoto() {
	}

	public ProductPhoto(Product product, byte[] imageData, int displayOrder) {
		this.product = product;
		this.imageData = imageData;
		this.displayOrder = displayOrder;
	}

	public Product getProduct() {
		return product;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
}
