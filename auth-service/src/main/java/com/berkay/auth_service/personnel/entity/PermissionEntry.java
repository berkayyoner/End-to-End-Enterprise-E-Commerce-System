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
 * One row per admin-panel page a {@link PermissionGroup} grants access to, e.g. "P0" with
 * add+edit+delete renders as the code "P0AED" (RULES.md's personnel permission scheme). View
 * access is implied by the row existing at all - there's no separate "canView" flag.
 */
@Entity
@Table(name = "permission_entry", uniqueConstraints = @UniqueConstraint(
		name = "uk_permission_entry_group_page", columnNames = {"permission_group_id", "page_code"}))
public class PermissionEntry extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "permission_group_id", nullable = false)
	private PermissionGroup permissionGroup;

	@Column(name = "page_code", nullable = false, length = 10)
	private String pageCode;

	@Column(name = "can_add", nullable = false)
	private boolean canAdd;

	@Column(name = "can_edit", nullable = false)
	private boolean canEdit;

	@Column(name = "can_delete", nullable = false)
	private boolean canDelete;

	protected PermissionEntry() {
	}

	public PermissionEntry(PermissionGroup permissionGroup, String pageCode, boolean canAdd, boolean canEdit,
			boolean canDelete) {
		this.permissionGroup = permissionGroup;
		this.pageCode = pageCode;
		this.canAdd = canAdd;
		this.canEdit = canEdit;
		this.canDelete = canDelete;
	}

	public PermissionGroup getPermissionGroup() {
		return permissionGroup;
	}

	public String getPageCode() {
		return pageCode;
	}

	public boolean canAdd() {
		return canAdd;
	}

	public boolean canEdit() {
		return canEdit;
	}

	public boolean canDelete() {
		return canDelete;
	}

	void updateCapabilities(boolean canAdd, boolean canEdit, boolean canDelete) {
		this.canAdd = canAdd;
		this.canEdit = canEdit;
		this.canDelete = canDelete;
	}

	/** e.g. "P0AED", "P2ED", "P3", "P4A" - the exact rendering RULES.md's examples use. */
	public String toCode() {
		StringBuilder code = new StringBuilder(pageCode);
		if (canAdd) {
			code.append('A');
		}
		if (canEdit) {
			code.append('E');
		}
		if (canDelete) {
			code.append('D');
		}
		return code.toString();
	}
}
