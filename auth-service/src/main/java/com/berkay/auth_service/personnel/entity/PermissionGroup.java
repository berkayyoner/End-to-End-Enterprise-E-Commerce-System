package com.berkay.auth_service.personnel.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A named bundle of page permissions, e.g. RULES.md's example "Manager: P0AED, P1AED, P2ED,
 * P3, P4A". Evaluation (task 1.2's "evaluation logic") lives here rather than in a separate
 * service since it's pure domain logic over this aggregate's own entries.
 *
 * <p>Name uniqueness is enforced only at the application layer
 * ({@code PermissionGroupRepository#existsByNameAndDeletedFalse}), deliberately not as a DB
 * unique constraint: a plain column-level constraint would keep blocking a name forever after
 * its group is soft-deleted (RULES.md - rows are never hard-deleted), and Oracle has no simple
 * declarative "unique among non-deleted rows" constraint without a function-based index, which
 * is more machinery than this needs before a migration tool exists to manage it.</p>
 */
@Entity
@Table(name = "permission_group")
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
		Optional<PermissionEntry> existing = findEntry(pageCode);
		if (existing.isPresent()) {
			// Updating the existing row in place (rather than remove+add) avoids a transient
			// unique-constraint violation: Hibernate flushes inserts before deletes, so a
			// remove-then-add for the same page code would briefly try to insert a duplicate
			// (permission_group_id, page_code) row before the orphaned one is deleted.
			existing.get().updateCapabilities(canAdd, canEdit, canDelete);
			return existing.get();
		}
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

	/**
	 * Decomposed Spring Security authorities for this group, e.g. entry "P0AED" becomes
	 * PERM_P0_VIEW, PERM_P0_ADD, PERM_P0_EDIT, PERM_P0_DELETE - one per capability, so a
	 * controller can require exactly the capability a given HTTP verb needs
	 * ({@code hasAuthority("PERM_P0_EDIT")}) instead of parsing a compact code string.
	 */
	public List<String> toAuthorities() {
		List<String> authorities = new ArrayList<>();
		for (PermissionEntry entry : entries) {
			authorities.add("PERM_" + entry.getPageCode() + "_VIEW");
			if (entry.canAdd()) {
				authorities.add("PERM_" + entry.getPageCode() + "_ADD");
			}
			if (entry.canEdit()) {
				authorities.add("PERM_" + entry.getPageCode() + "_EDIT");
			}
			if (entry.canDelete()) {
				authorities.add("PERM_" + entry.getPageCode() + "_DELETE");
			}
		}
		return authorities;
	}
}
