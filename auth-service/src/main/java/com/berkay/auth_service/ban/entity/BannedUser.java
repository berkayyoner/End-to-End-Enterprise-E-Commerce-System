package com.berkay.auth_service.ban.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * RULES.md: banning a "Users" page account copies its info here (a frozen snapshot, not a live
 * join to app_user, since the source account keeps changing shape over time and we still want
 * to know exactly what it looked like when it was banned) - id numbers, phone number, IP
 * address (best-effort - no login-IP capture exists yet, task 1.7 may add one) and email. The
 * originating AppUser is separately soft-deleted (BaseEntity#softDelete, never hard-deleted) so
 * it can no longer log in.
 */
@Entity
@Table(name = "banned_user")
public class BannedUser extends BaseEntity {

	@Column(name = "original_app_user_id", nullable = false)
	private Long originalAppUserId;

	@Column(name = "email", nullable = false, length = 254)
	private String email;

	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Column(name = "id_numbers", length = 500)
	private String idNumbers;

	@Column(name = "ip_address", length = 64)
	private String ipAddress;

	@Column(name = "reason", nullable = false, length = 500)
	private String reason;

	protected BannedUser() {
	}

	public BannedUser(Long originalAppUserId, String email, String phoneNumber, String idNumbers, String ipAddress,
			String reason) {
		this.originalAppUserId = originalAppUserId;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.idNumbers = idNumbers;
		this.ipAddress = ipAddress;
		this.reason = reason;
	}

	public Long getOriginalAppUserId() {
		return originalAppUserId;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getIdNumbers() {
		return idNumbers;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getReason() {
		return reason;
	}
}
