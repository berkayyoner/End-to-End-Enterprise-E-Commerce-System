package com.berkay.auth_service.personnel.security;

import com.berkay.auth_service.personnel.entity.Personnel;
import com.berkay.auth_service.personnel.repository.PersonnelRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Backs the personnel-only login (RULES.md: "personnel login separate from public users").
 * Deliberately a distinct bean/class from {@code AppUserDetailsService} rather than one lookup
 * across both tables, so a public account can never authenticate on the personnel login path.
 */
@Service
public class PersonnelDetailsService implements UserDetailsService {

	private final PersonnelRepository personnelRepository;

	public PersonnelDetailsService(PersonnelRepository personnelRepository) {
		this.personnelRepository = personnelRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		Personnel personnel = personnelRepository.findByEmailWithPermissions(email.trim().toLowerCase())
				.orElseThrow(() -> new UsernameNotFoundException("No personnel account for email: " + email));

		String[] authorities = personnel.getPermissionGroup().toCodes().stream()
				.map(code -> "PERM_" + code)
				.toArray(String[]::new);

		return User.withUsername(personnel.getEmail())
				.password(personnel.getPasswordHash())
				.disabled(personnel.isDeleted())
				.authorities(authorities)
				.build();
	}
}
