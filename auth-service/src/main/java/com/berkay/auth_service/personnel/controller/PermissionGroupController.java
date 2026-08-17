package com.berkay.auth_service.personnel.controller;

import com.berkay.auth_service.personnel.dto.PermissionGroupRequest;
import com.berkay.auth_service.personnel.dto.PermissionGroupResponse;
import com.berkay.auth_service.personnel.service.PermissionGroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RULES.md: "the Permissions page... lets personnels with P0 permission create, edit and
 * delete permission groups." Each verb requires the matching P0 capability authority
 * (see PermissionGroup#toAuthorities / PersonnelDetailsService).
 */
@RestController
@RequestMapping("/personnel/permission-groups")
public class PermissionGroupController {

	private final PermissionGroupService permissionGroupService;

	public PermissionGroupController(PermissionGroupService permissionGroupService) {
		this.permissionGroupService = permissionGroupService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P0_VIEW')")
	public List<PermissionGroupResponse> list() {
		return permissionGroupService.list();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P0_VIEW')")
	public PermissionGroupResponse get(@PathVariable Long id) {
		return permissionGroupService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P0_ADD')")
	public ResponseEntity<PermissionGroupResponse> create(@Valid @RequestBody PermissionGroupRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(permissionGroupService.create(request));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P0_EDIT')")
	public PermissionGroupResponse update(@PathVariable Long id, @Valid @RequestBody PermissionGroupRequest request) {
		return permissionGroupService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PERM_P0_DELETE')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		permissionGroupService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
