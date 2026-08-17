package com.berkay.auth_service.personnel.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.activitylog.ActorType;
import com.berkay.auth_service.exception.CannotRemoveLastP0AdminException;
import com.berkay.auth_service.exception.DuplicatePersonnelEmailException;
import com.berkay.auth_service.exception.PermissionGroupNotFoundException;
import com.berkay.auth_service.exception.PersonnelNotFoundException;
import com.berkay.auth_service.personnel.dto.PermissionGroupAssignmentRequest;
import com.berkay.auth_service.personnel.dto.PersonnelCreateRequest;
import com.berkay.auth_service.personnel.dto.PersonnelListResponse;
import com.berkay.auth_service.personnel.entity.PermissionGroup;
import com.berkay.auth_service.personnel.entity.Personnel;
import com.berkay.auth_service.personnel.repository.PermissionGroupRepository;
import com.berkay.auth_service.personnel.repository.PersonnelRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles personnel account management: create, list, and permission group reassignment.
 * Enforces two critical guards:
 * 1. Cannot reassign the bootstrap admin's permission group (Super Admin first name + last name)
 * 2. Cannot remove the last remaining P0 admin by reassigning their permission group
 */
@Service
public class PersonnelManagementService {

	private static final String BOOTSTRAP_FIRST_NAME = "Super";
	private static final String BOOTSTRAP_LAST_NAME = "Admin";

	private final PersonnelRepository personnelRepository;
	private final PermissionGroupRepository permissionGroupRepository;
	private final PasswordEncoder passwordEncoder;
	private final ActivityLogClient activityLogClient;

	public PersonnelManagementService(PersonnelRepository personnelRepository,
			PermissionGroupRepository permissionGroupRepository,
			PasswordEncoder passwordEncoder,
			ActivityLogClient activityLogClient) {
		this.personnelRepository = personnelRepository;
		this.permissionGroupRepository = permissionGroupRepository;
		this.passwordEncoder = passwordEncoder;
		this.activityLogClient = activityLogClient;
	}

	@Transactional(readOnly = true)
	public List<PersonnelListResponse> list() {
		return personnelRepository.findAllActiveWithPermissionGroups()
				.stream()
				.map(PersonnelListResponse::from)
				.toList();
	}

	@Transactional
	public PersonnelListResponse create(PersonnelCreateRequest request) {
		if (personnelRepository.existsByEmail(request.email().toLowerCase().trim())) {
			throw new DuplicatePersonnelEmailException(request.email());
		}

		PermissionGroup group = permissionGroupRepository.findActiveByIdWithEntries(request.permissionGroupId())
				.orElseThrow(() -> new PermissionGroupNotFoundException(request.permissionGroupId()));

		Personnel personnel = new Personnel(
				request.email().trim().toLowerCase(),
				passwordEncoder.encode(request.password()),
				request.firstName(),
				request.lastName(),
				group);

		Personnel saved = personnelRepository.save(personnel);
		activityLogClient.log(ActorType.PERSONNEL, null, "PERSONNEL_CREATED",
				"personnelId=" + saved.getId() + ", email=" + saved.getEmail());

		return PersonnelListResponse.from(saved);
	}

	@Transactional
	public PersonnelListResponse reassignPermissionGroup(Long personnelId, PermissionGroupAssignmentRequest request) {
		Personnel personnel = personnelRepository.findById(personnelId)
				.orElseThrow(() -> new PersonnelNotFoundException("Personnel not found with id: " + personnelId));

		// Guard: cannot reassign the bootstrap admin's permission group
		if (isBootstrapAdmin(personnel)) {
			throw new CannotRemoveLastP0AdminException();
		}

		PermissionGroup currentGroup = personnel.getPermissionGroup();
		boolean currentlyHasP0 = currentGroup.hasView("P0");

		PermissionGroup newGroup = permissionGroupRepository.findActiveByIdWithEntries(request.permissionGroupId())
				.orElseThrow(() -> new PermissionGroupNotFoundException(request.permissionGroupId()));

		boolean newGroupHasP0 = newGroup.hasView("P0");

		// Guard: if removing P0 access and this is the last P0 admin, reject it
		if (currentlyHasP0 && !newGroupHasP0) {
			long p0AdminCount = personnelRepository.countWithP0Access();
			if (p0AdminCount <= 1) {
				throw new CannotRemoveLastP0AdminException();
			}
		}

		personnel.reassignPermissionGroup(newGroup);
		Personnel saved = personnelRepository.save(personnel);
		activityLogClient.log(ActorType.PERSONNEL, null, "PERSONNEL_PERMISSION_GROUP_CHANGED",
				"personnelId=" + saved.getId() + ", email=" + saved.getEmail() +
				", newGroupId=" + newGroup.getId() + ", newGroupName=" + newGroup.getName());

		return PersonnelListResponse.from(saved);
	}

	private boolean isBootstrapAdmin(Personnel personnel) {
		return BOOTSTRAP_FIRST_NAME.equals(personnel.getFirstName()) &&
				BOOTSTRAP_LAST_NAME.equals(personnel.getLastName());
	}
}
