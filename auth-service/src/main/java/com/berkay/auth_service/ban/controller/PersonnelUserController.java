package com.berkay.auth_service.ban.controller;

import com.berkay.auth_service.ban.dto.BanUserRequest;
import com.berkay.auth_service.ban.dto.BannedUserResponse;
import com.berkay.auth_service.ban.service.BanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RULES.md's "Users" page (PERMISSIONS.md: P3). Only the ban action and the banned-users list
 * exist so far; listing/editing/soft-deleting ordinary accounts is Phase 7.2's job, added under
 * this same page code.
 */
@RestController
@RequestMapping("/personnel/users")
public class PersonnelUserController {

	private final BanService banService;

	public PersonnelUserController(BanService banService) {
		this.banService = banService;
	}

	@PostMapping("/{id}/ban")
	@PreAuthorize("hasAuthority('PERM_P3_DELETE')")
	public BannedUserResponse ban(@PathVariable Long id, @Valid @RequestBody BanUserRequest request) {
		return banService.ban(id, request.reason());
	}

	@GetMapping("/banned")
	@PreAuthorize("hasAuthority('PERM_P3_VIEW')")
	public Page<BannedUserResponse> listBanned(Pageable pageable) {
		return banService.list(pageable);
	}
}
