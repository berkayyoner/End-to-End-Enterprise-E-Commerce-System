package com.berkay.auth_service.user.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A public-application account. RULES.md's "Customer"/"Seller" permission groups are not
 * separate tables: every account starts as a customer and becomes a seller in place once its
 * seller application (task 1.5) is approved - {@link #seller} flips from false to true on the
 * same row rather than creating a new record.
 */
@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(name = "uk_app_user_email", columnNames = "email"))
public class AppUser extends BaseEntity {

	@Column(name = "email", nullable = false, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Column(name = "id_verified", nullable = false)
	private boolean idVerified = false;

	@Column(name = "seller", nullable = false)
	private boolean seller = false;

	protected AppUser() {
	}

	public AppUser(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public boolean isIdVerified() {
		return idVerified;
	}

	public boolean isSeller() {
		return seller;
	}

	public void markIdVerified() {
		this.idVerified = true;
	}

	public void approveAsSeller() {
		this.seller = true;
	}
}
