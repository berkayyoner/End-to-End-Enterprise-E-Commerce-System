package com.berkay.auth_service.personnel.service;

import com.berkay.auth_service.exception.DuplicatePermissionGroupNameException;
import com.berkay.auth_service.exception.PermissionGroupNotFoundException;
import com.berkay.auth_service.personnel.dto.PermissionEntryDto;
import com.berkay.auth_service.personnel.dto.PermissionGroupRequest;
import com.berkay.auth_service.personnel.dto.PermissionGroupResponse;
import com.berkay.auth_service.personnel.entity.PermissionEntry;
import com.berkay.auth_service.personnel.entity.PermissionGroup;
import com.berkay.auth_service.personnel.repository.PermissionGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Backs the P0-only "Permissions" page (RULES.md): create/edit/delete permission groups and
 * their page codes. Access control itself lives in SecurityConfig/PermissionGroupController
 * (hasAuthority checks against PermissionGroup#toAuthorities) - this service only enforces
 * data invariants (unique names, groups are never hard-deleted).
 */
@Service
public class PermissionGroupService {

	private final PermissionGroupRepository permissionGroupRepository;

	public PermissionGroupService(PermissionGroupRepository permissionGroupRepository) {
		this.permissionGroupRepository = permissionGroupRepository;
	}

	@Transactional(readOnly = true)
	public List<PermissionGroupResponse> list() {
		return permissionGroupRepository.findAllActiveWithEntries().stream()
				.map(PermissionGroupResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public PermissionGroupResponse get(Long id) {
		return PermissionGroupResponse.from(findActiveOrThrow(id));
	}

	@Transactional
	public PermissionGroupResponse create(PermissionGroupRequest request) {
		if (permissionGroupRepository.existsByNameAndDeletedFalse(request.name())) {
			throw new DuplicatePermissionGroupNameException(request.name());
		}

		PermissionGroup group = new PermissionGroup(request.name());
		applyEntries(group, request.entries());

		return PermissionGroupResponse.from(permissionGroupRepository.save(group));
	}

	@Transactional
	public PermissionGroupResponse update(Long id, PermissionGroupRequest request) {
		PermissionGroup group = findActiveOrThrow(id);

		if (!group.getName().equals(request.name()) && permissionGroupRepository.existsByNameAndDeletedFalse(request.name())) {
			throw new DuplicatePermissionGroupNameException(request.name());
		}

		group.rename(request.name());
		Set<String> requestedPageCodes = request.entries().stream().map(PermissionEntryDto::pageCode)
				.collect(Collectors.toSet());
		group.getEntries().stream()
				.map(PermissionEntry::getPageCode)
				.filter(pageCode -> !requestedPageCodes.contains(pageCode))
				.toList()
				.forEach(group::revoke);
		applyEntries(group, request.entries());

		return PermissionGroupResponse.from(permissionGroupRepository.save(group));
	}

	@Transactional
	public void delete(Long id) {
		PermissionGroup group = findActiveOrThrow(id);
		group.softDelete();
		permissionGroupRepository.save(group);
	}

	private void applyEntries(PermissionGroup group, List<PermissionEntryDto> entries) {
		for (PermissionEntryDto entry : entries) {
			group.grant(entry.pageCode(), entry.canAdd(), entry.canEdit(), entry.canDelete());
		}
	}

	private PermissionGroup findActiveOrThrow(Long id) {
		return permissionGroupRepository.findActiveByIdWithEntries(id)
				.orElseThrow(() -> new PermissionGroupNotFoundException(id));
	}
}
