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
	void mapsPermissionCodesAndAccountTypeToAuthorities() {
		Jwt jwt = jwtWithClaims(Map.of(
				"permissions", List.of("P0AED", "P2ED"),
				"account_type", "PERSONNEL"));

		Collection<GrantedAuthority> authorities = converter.convert(jwt);

		assertThat(authorities)
				.extracting(GrantedAuthority::getAuthority)
				.containsExactlyInAnyOrder("PERM_P0AED", "PERM_P2ED", "ACCOUNT_PERSONNEL");
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
