package com.berkay.auth_service.ban.controller;

import com.berkay.auth_service.ban.dto.BanUserRequest;
import com.berkay.auth_service.ban.dto.BannedUserResponse;
import com.berkay.auth_service.ban.service.BanService;
import com.berkay.auth_service.user.dto.AppUserEditRequest;
import com.berkay.auth_service.user.dto.AppUserListResponse;
import com.berkay.auth_service.user.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * RULES.md's "Users" page (PERMISSIONS.md: P3). Phase 7.2 adds full CRUD (list/edit/soft-delete)
 * for ordinary accounts under the existing ban action and banned-users list from task 1.6.
 */
@RestController
@RequestMapping("/personnel/users")
public class PersonnelUserController {

	private final BanService banService;
	private final UserManagementService userManagementService;

	public PersonnelUserController(BanService banService, UserManagementService userManagementService) {
		this.banService = banService;
		this.userManagementService = userManagementService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P3_VIEW')")
	public Page<AppUserListResponse> listAll(Pageable pageable) {
		return userManagementService.listAllUsers(pageable);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P3_EDIT')")
	public AppUserListResponse edit(@PathVariable Long id, @Valid @RequestBody AppUserEditRequest request) {
		return userManagementService.editUser(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P3_DELETE')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		userManagementService.deleteUser(id);
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
