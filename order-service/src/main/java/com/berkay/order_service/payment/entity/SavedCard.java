package com.berkay.order_service.payment.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "saved_card")
public class SavedCard extends BaseEntity {

	@Column(name = "buyer_id", nullable = false)
	private String buyerId;

	@Column(name = "card_number_encrypted", nullable = false, length = 512)
	private String cardNumberEncrypted;

	@Column(name = "expiry_month", nullable = false)
	private Integer expiryMonth;

	@Column(name = "expiry_year", nullable = false)
	private Integer expiryYear;

	@Column(name = "card_holder_name", nullable = false, length = 100)
	private String cardHolderName;

	public SavedCard() {}

	public SavedCard(String buyerId, String cardNumberEncrypted, Integer expiryMonth, Integer expiryYear, String cardHolderName) {
		this.buyerId = buyerId;
		this.cardNumberEncrypted = cardNumberEncrypted;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
		this.cardHolderName = cardHolderName;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getCardNumberEncrypted() {
		return cardNumberEncrypted;
	}

	public void setCardNumberEncrypted(String cardNumberEncrypted) {
		this.cardNumberEncrypted = cardNumberEncrypted;
	}

	public Integer getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(Integer expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public Integer getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(Integer expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
}
