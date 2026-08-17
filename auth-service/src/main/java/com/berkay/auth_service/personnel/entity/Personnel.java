package com.berkay.auth_service.personnel.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * An internal admin/moderator account - RULES.md keeps these entirely separate from public
 * {@link com.berkay.auth_service.user.entity.AppUser} accounts (different table, different
 * login endpoint, different token audience). Every personnel belongs to exactly one
 * {@link PermissionGroup}; there is no personnel with no group.
 */
@Entity
@Table(name = "personnel", uniqueConstraints = @UniqueConstraint(name = "uk_personnel_email", columnNames = "email"))
public class Personnel extends BaseEntity {

	@Column(name = "email", nullable = false, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "permission_group_id", nullable = false)
	private PermissionGroup permissionGroup;

	protected Personnel() {
	}

	public Personnel(String email, String passwordHash, String firstName, String lastName,
			PermissionGroup permissionGroup) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.permissionGroup = permissionGroup;
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

	public PermissionGroup getPermissionGroup() {
		return permissionGroup;
	}

	public void reassignPermissionGroup(PermissionGroup permissionGroup) {
		this.permissionGroup = permissionGroup;
	}
}
