package com.berkay.auth_service.personnel.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A named bundle of page permissions, e.g. RULES.md's example "Manager: P0AED, P1AED, P2ED,
 * P3, P4A". Evaluation (task 1.2's "evaluation logic") lives here rather than in a separate
 * service since it's pure domain logic over this aggregate's own entries.
 */
@Entity
@Table(name = "permission_group", uniqueConstraints = @UniqueConstraint(name = "uk_permission_group_name", columnNames = "name"))
public class PermissionGroup extends BaseEntity {

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@OneToMany(mappedBy = "permissionGroup", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	private List<PermissionEntry> entries = new ArrayList<>();

	protected PermissionGroup() {
	}

	public PermissionGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void rename(String name) {
		this.name = name;
	}

	public List<PermissionEntry> getEntries() {
		return List.copyOf(entries);
	}

	public PermissionEntry grant(String pageCode, boolean canAdd, boolean canEdit, boolean canDelete) {
		entries.removeIf(entry -> entry.getPageCode().equals(pageCode));
		PermissionEntry entry = new PermissionEntry(this, pageCode, canAdd, canEdit, canDelete);
		entries.add(entry);
		return entry;
	}

	public void revoke(String pageCode) {
		entries.removeIf(entry -> entry.getPageCode().equals(pageCode));
	}

	public boolean hasView(String pageCode) {
		return findEntry(pageCode).isPresent();
	}

	public boolean hasAdd(String pageCode) {
		return findEntry(pageCode).map(PermissionEntry::canAdd).orElse(false);
	}

	public boolean hasEdit(String pageCode) {
		return findEntry(pageCode).map(PermissionEntry::canEdit).orElse(false);
	}

	public boolean hasDelete(String pageCode) {
		return findEntry(pageCode).map(PermissionEntry::canDelete).orElse(false);
	}

	private Optional<PermissionEntry> findEntry(String pageCode) {
		return entries.stream().filter(entry -> entry.getPageCode().equals(pageCode)).findFirst();
	}

	/** e.g. ["P0AED", "P1AED", "P2ED", "P3", "P4A"] - sorted for a deterministic JWT claim. */
	public List<String> toCodes() {
		return entries.stream()
				.sorted(Comparator.comparing(PermissionEntry::getPageCode))
				.map(PermissionEntry::toCode)
				.toList();
	}
}
