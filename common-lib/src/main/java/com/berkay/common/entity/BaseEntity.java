package com.berkay.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * Base type for every persistent entity across Berkay's services.
 * Rows are never hard-deleted (see RULES.md) - {@link #softDelete()} marks a row
 * deleted while keeping it, and every row carries who/when it was created and last changed.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, length = 100)
	private String createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", length = 100)
	private String updatedBy;

	@Column(name = "is_deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	public void softDelete() {
		this.deleted = true;
		this.deletedAt = Instant.now();
	}

	public void restore() {
		this.deleted = false;
		this.deletedAt = null;
	}

	public Long getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Instant getDeletedAt() {
		return deletedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BaseEntity other)) {
			return false;
		}
		return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
