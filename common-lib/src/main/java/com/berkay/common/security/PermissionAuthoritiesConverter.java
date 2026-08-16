package com.berkay.common.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Maps a resource-server JWT (issued by auth-service, see OAUTH2.md) to Spring Security
 * authorities: one {@code PERM_<code>} per RULES.md permission code in the "permissions" claim
 * (e.g. {@code PERM_P2ED}), plus one {@code ACCOUNT_<type>} for the "account_type" claim.
 * Every resource server wires this the same way so the claim-to-authority mapping never drifts
 * between services.
 */
public class PermissionAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	public static final String PERMISSIONS_CLAIM = "permissions";
	public static final String ACCOUNT_TYPE_CLAIM = "account_type";
	public static final String PERMISSION_AUTHORITY_PREFIX = "PERM_";
	public static final String ACCOUNT_TYPE_AUTHORITY_PREFIX = "ACCOUNT_";

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		List<String> permissions = jwt.getClaimAsStringList(PERMISSIONS_CLAIM);
		if (permissions != null) {
			for (String code : permissions) {
				authorities.add(new SimpleGrantedAuthority(PERMISSION_AUTHORITY_PREFIX + code));
			}
		}

		String accountType = jwt.getClaimAsString(ACCOUNT_TYPE_CLAIM);
		if (accountType != null && !accountType.isBlank()) {
			authorities.add(new SimpleGrantedAuthority(ACCOUNT_TYPE_AUTHORITY_PREFIX + accountType));
		}

		return authorities;
	}
}
