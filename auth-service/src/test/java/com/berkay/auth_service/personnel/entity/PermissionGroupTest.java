package com.berkay.auth_service.personnel.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionGroupTest {

	@Test
	void rendersCodesExactlyLikeRulesExamples() {
		PermissionGroup manager = new PermissionGroup("Manager");
		manager.grant("P0", true, true, true);
		manager.grant("P1", true, true, true);
		manager.grant("P2", false, true, true);
		manager.grant("P3", false, false, false);
		manager.grant("P4", true, false, false);

		assertThat(manager.toCodes()).containsExactly("P0AED", "P1AED", "P2ED", "P3", "P4A");
	}

	@Test
	void evaluatesViewAddEditDeletePerPage() {
		PermissionGroup group = new PermissionGroup("Support");
		group.grant("P2", false, true, false);

		assertThat(group.hasView("P2")).isTrue();
		assertThat(group.hasAdd("P2")).isFalse();
		assertThat(group.hasEdit("P2")).isTrue();
		assertThat(group.hasDelete("P2")).isFalse();
	}

	@Test
	void hasNoAccessToAPageThatWasNeverGranted() {
		PermissionGroup group = new PermissionGroup("Support");

		assertThat(group.hasView("P9")).isFalse();
		assertThat(group.hasAdd("P9")).isFalse();
		assertThat(group.hasEdit("P9")).isFalse();
		assertThat(group.hasDelete("P9")).isFalse();
	}

	@Test
	void regrantingAPageReplacesThePreviousEntryRatherThanDuplicatingIt() {
		PermissionGroup group = new PermissionGroup("Support");
		group.grant("P2", false, false, false);
		group.grant("P2", true, true, true);

		assertThat(group.getEntries()).hasSize(1);
		assertThat(group.toCodes()).containsExactly("P2AED");
	}

	@Test
	void revokingRemovesThePage() {
		PermissionGroup group = new PermissionGroup("Support");
		group.grant("P2", true, true, true);

		group.revoke("P2");

		assertThat(group.hasView("P2")).isFalse();
		assertThat(group.toCodes()).isEmpty();
	}
}
