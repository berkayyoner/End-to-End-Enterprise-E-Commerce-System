package com.berkay.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionAuthoritiesConverterTest {

	private final PermissionAuthoritiesConverter converter = new PermissionAuthoritiesConverter();

	@Test
	void decomposesPermissionCodesAndAccountTypeToAuthorities() {
		Jwt jwt = jwtWithClaims(Map.of(
				"permissions", List.of("P0AED", "P2ED"),
				"account_type", "PERSONNEL"));

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		// P0AED decomposes to PERM_P0_VIEW, PERM_P0_ADD, PERM_P0_EDIT, PERM_P0_DELETE (4 authorities)
		// P2ED decomposes to PERM_P2_VIEW, PERM_P2_EDIT, PERM_P2_DELETE (3 authorities, no ADD)
		// Plus ACCOUNT_PERSONNEL (1 authority)
		// Total: 8 authorities
		assertThat(authorities)
				.extracting(GrantedAuthority::getAuthority)
				.containsExactlyInAnyOrder(
						"PERM_P0_VIEW", "PERM_P0_ADD", "PERM_P0_EDIT", "PERM_P0_DELETE",
						"PERM_P2_VIEW", "PERM_P2_EDIT", "PERM_P2_DELETE",
						"ACCOUNT_PERSONNEL");
	}

	@Test
	void decomposesPermissionWithAllCapabilities() {
		// P4AED should decompose to VIEW, ADD, EDIT, DELETE
		Jwt jwt = jwtWithClaims(Map.of("permissions", List.of("P4AED")));

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		assertThat(authorities)
				.extracting(GrantedAuthority::getAuthority)
				.containsExactlyInAnyOrder("PERM_P4_VIEW", "PERM_P4_ADD", "PERM_P4_EDIT", "PERM_P4_DELETE");
	}

	@Test
	void decomposesPermissionWithViewOnly() {
		// P3 (no suffix) should decompose to only VIEW
		Jwt jwt = jwtWithClaims(Map.of("permissions", List.of("P3")));

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		assertThat(authorities)
				.extracting(GrantedAuthority::getAuthority)
				.containsExactly("PERM_P3_VIEW");
	}

	@Test
	void decomposesPermissionWithPartialCapabilities() {
		// P0ED should decompose to VIEW, EDIT, DELETE (no ADD)
		Jwt jwt = jwtWithClaims(Map.of("permissions", List.of("P0ED")));

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		assertThat(authorities)
				.extracting(GrantedAuthority::getAuthority)
				.containsExactlyInAnyOrder("PERM_P0_VIEW", "PERM_P0_EDIT", "PERM_P0_DELETE");
	}

	@Test
	void handlesTokenWithNoPermissionsClaim() {
		Jwt jwt = jwtWithClaims(Map.of("account_type", "CUSTOMER"));

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		assertThat(authorities)
				.extracting(GrantedAuthority::getAuthority)
				.containsExactly("ACCOUNT_CUSTOMER");
	}

	@Test
	void returnsEmptyAuthoritiesWhenNoRelevantClaimsPresent() {
		Jwt jwt = jwtWithClaims(Map.of());

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		assertThat(authorities).isEmpty();
	}

	private static Jwt jwtWithClaims(Map<String, Object> claims) {
		return Jwt.withTokenValue("test-token")
				.header("alg", "none")
				.claims(c -> c.putAll(claims))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(60))
				.build();
	}
}
