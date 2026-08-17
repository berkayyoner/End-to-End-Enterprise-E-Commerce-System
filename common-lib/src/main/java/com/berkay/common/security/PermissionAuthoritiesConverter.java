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
 * authorities: decomposes each compact permission code in the "permissions" claim
 * (e.g. {@code "P2ED"}) into decomposed authorities matching RULES.md's scheme:
 * <ul>
 *   <li>{@code PERM_<pageCode>_VIEW} always (presence of the code implies view access)</li>
 *   <li>{@code PERM_<pageCode>_ADD} if the suffix contains 'A'</li>
 *   <li>{@code PERM_<pageCode>_EDIT} if the suffix contains 'E'</li>
 *   <li>{@code PERM_<pageCode>_DELETE} if the suffix contains 'D'</li>
 * </ul>
 * Also maps {@code account_type} claim to one {@code ACCOUNT_<type>} authority.
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
				decomposePermission(code, authorities);
			}
		}

		String accountType = jwt.getClaimAsString(ACCOUNT_TYPE_CLAIM);
		if (accountType != null && !accountType.isBlank()) {
			authorities.add(new SimpleGrantedAuthority(ACCOUNT_TYPE_AUTHORITY_PREFIX + accountType));
		}

		return authorities;
	}

	/**
	 * Decomposes a compact permission code (e.g. "P0AED", "P2ED", "P3") into individual
	 * authorities following the pattern (P\d+)([AED]*):
	 * - Page code is the "P" followed by digits (e.g. "P0", "P2", "P3")
	 * - Suffix is optional letters (A/E/D, e.g. "AED", "ED", or empty)
	 * Always emits PERM_<pageCode>_VIEW, then conditionally emits ADD/EDIT/DELETE.
	 */
	private void decomposePermission(String code, List<GrantedAuthority> authorities) {
		// Match pattern (P\d+)([AED]*)
		if (!code.startsWith("P")) {
			// Invalid code, skip silently
			return;
		}

		int i = 1;
		while (i < code.length() && Character.isDigit(code.charAt(i))) {
			i++;
		}
		if (i == 1) {
			// No digits after P, invalid code
			return;
		}

		String pageCode = code.substring(0, i); // e.g. "P0", "P2", "P4"
		String suffix = code.substring(i); // e.g. "AED", "ED", ""

		// Always emit VIEW
		authorities.add(new SimpleGrantedAuthority(PERMISSION_AUTHORITY_PREFIX + pageCode + "_VIEW"));

		// Emit conditionally based on suffix
		if (suffix.contains("A")) {
			authorities.add(new SimpleGrantedAuthority(PERMISSION_AUTHORITY_PREFIX + pageCode + "_ADD"));
		}
		if (suffix.contains("E")) {
			authorities.add(new SimpleGrantedAuthority(PERMISSION_AUTHORITY_PREFIX + pageCode + "_EDIT"));
		}
		if (suffix.contains("D")) {
			authorities.add(new SimpleGrantedAuthority(PERMISSION_AUTHORITY_PREFIX + pageCode + "_DELETE"));
		}
	}
}
