package com.berkay.auth_service.user.security;

import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Backs Spring Security's form login with {@link AppUser} rows. The username is the account's
 * email. A soft-deleted (banned, task 1.6) account is reported as disabled rather than "not
 * found" so a ban shows up as an explicit account-disabled error, not a generic bad-credentials.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	public AppUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		AppUser user = appUserRepository.findByEmail(email.trim().toLowerCase())
				.orElseThrow(() -> new UsernameNotFoundException("No account for email: " + email));

		List<String> authorities = new ArrayList<>();
		authorities.add("ROLE_CUSTOMER");
		if (user.isSeller()) {
			authorities.add("ROLE_SELLER");
		}

		return User.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.disabled(user.isDeleted())
				.authorities(authorities.toArray(String[]::new))
				.build();
	}
}
