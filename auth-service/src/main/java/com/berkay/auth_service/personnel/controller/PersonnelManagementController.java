package com.berkay.auth_service.personnel.controller;

import com.berkay.auth_service.personnel.dto.PermissionGroupAssignmentRequest;
import com.berkay.auth_service.personnel.dto.PersonnelCreateRequest;
import com.berkay.auth_service.personnel.dto.PersonnelListResponse;
import com.berkay.auth_service.personnel.service.PersonnelManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RULES.md: "There must be a 'Personnel' page... for creating personnel accounts and
 * editing their permission groups" (PERMISSIONS.md: P7). Session-based auth for
 * personnel-facing endpoints - mirrors PermissionGroupController's pattern.
 */
@RestController
@RequestMapping("/personnel/personnel-accounts")
public class PersonnelManagementController {

	private final PersonnelManagementService personnelManagementService;

	public PersonnelManagementController(PersonnelManagementService personnelManagementService) {
		this.personnelManagementService = personnelManagementService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_P7_VIEW')")
	public List<PersonnelListResponse> list() {
		return personnelManagementService.list();
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_P7_ADD')")
	public ResponseEntity<PersonnelListResponse> create(@Valid @RequestBody PersonnelCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(personnelManagementService.create(request));
	}

	@PutMapping("/{id}/permission-group")
	@PreAuthorize("hasAuthority('PERM_P7_EDIT')")
	public PersonnelListResponse reassignPermissionGroup(
			@PathVariable Long id,
			@Valid @RequestBody PermissionGroupAssignmentRequest request) {
		return personnelManagementService.reassignPermissionGroup(id, request);
	}
}
