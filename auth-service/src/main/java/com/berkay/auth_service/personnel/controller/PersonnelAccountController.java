package com.berkay.auth_service.personnel.controller;

import com.berkay.auth_service.exception.PersonnelNotFoundException;
import com.berkay.auth_service.personnel.dto.PersonnelResponse;
import com.berkay.auth_service.personnel.repository.PersonnelRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Backs the SPA's personnel session profile (task 1.9): the current Personnel's own profile,
 * read from the session established by SecurityConfig's personnel chain (/personnel/**).
 * Matched by the personnel chain, so the caller is always an authenticated Personnel.
 */
@RestController
public class PersonnelAccountController {

	private final PersonnelRepository personnelRepository;

	public PersonnelAccountController(PersonnelRepository personnelRepository) {
		this.personnelRepository = personnelRepository;
	}

	@GetMapping("/personnel/me")
	public PersonnelResponse me(Authentication authentication) {
		return personnelRepository.findByEmailWithPermissionsAndNotDeleted(authentication.getName())
				.map(PersonnelResponse::from)
				.orElseThrow(() -> new PersonnelNotFoundException("Personnel not found"));
	}
}
