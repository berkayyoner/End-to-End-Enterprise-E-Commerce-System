package com.berkay.auth_service.personnel.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonnelManagementServiceTest {

	@Mock
	private PersonnelRepository personnelRepository;

	@Mock
	private PermissionGroupRepository permissionGroupRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ActivityLogClient activityLogClient;

	@InjectMocks
	private PersonnelManagementService service;

	@Test
	void createSucceedsWithValidRequest() {
		PermissionGroup group = new PermissionGroup("Manager");
		group.grant("P1", true, false, false);

		PersonnelCreateRequest request = new PersonnelCreateRequest(
				"newadmin@example.com",
				"password123",
				"John",
				"Doe",
				1L);

		when(personnelRepository.existsByEmail("newadmin@example.com")).thenReturn(false);
		when(permissionGroupRepository.findActiveByIdWithEntries(1L)).thenReturn(Optional.of(group));
		when(passwordEncoder.encode("password123")).thenReturn("hashed-password");

		Personnel saved = new Personnel("newadmin@example.com", "hashed-password", "John", "Doe", group);
		ReflectionTestUtils.setField(saved, "id", 10L);

		when(personnelRepository.save(any(Personnel.class))).thenReturn(saved);

		PersonnelListResponse response = service.create(request);

		assertThat(response.email()).isEqualTo("newadmin@example.com");
		assertThat(response.firstName()).isEqualTo("John");
		assertThat(response.lastName()).isEqualTo("Doe");
		verify(activityLogClient).log(any(), any(), any(), any());
	}

	@Test
	void createThrowsDuplicateEmailException() {
		PersonnelCreateRequest request = new PersonnelCreateRequest(
				"existing@example.com",
				"password123",
				"John",
				"Doe",
				1L);

		when(personnelRepository.existsByEmail("existing@example.com")).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(DuplicatePersonnelEmailException.class)
				.hasMessage("A personnel account with email 'existing@example.com' already exists");
	}

	@Test
	void createThrowsPermissionGroupNotFound() {
		PersonnelCreateRequest request = new PersonnelCreateRequest(
				"newadmin@example.com",
				"password123",
				"John",
				"Doe",
				999L);

		when(personnelRepository.existsByEmail("newadmin@example.com")).thenReturn(false);
		when(permissionGroupRepository.findActiveByIdWithEntries(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(PermissionGroupNotFoundException.class);
	}

	@Test
	void listReturnsAllActivePersonnel() {
		PermissionGroup group1 = new PermissionGroup("Admin");
		group1.grant("P0", true, true, true);

		PermissionGroup group2 = new PermissionGroup("Moderator");
		group2.grant("P1", true, false, false);

		Personnel p1 = new Personnel("admin@example.com", "hash1", "John", "Doe", group1);
		ReflectionTestUtils.setField(p1, "id", 1L);

		Personnel p2 = new Personnel("mod@example.com", "hash2", "Jane", "Smith", group2);
		ReflectionTestUtils.setField(p2, "id", 2L);

		when(personnelRepository.findAllActiveWithPermissionGroups()).thenReturn(List.of(p1, p2));

		List<PersonnelListResponse> responses = service.list();

		assertThat(responses)
				.hasSize(2)
				.extracting(PersonnelListResponse::email)
				.containsExactly("admin@example.com", "mod@example.com");
	}

	@Test
	void reassignPermissionGroupSucceeds() {
		PermissionGroup oldGroup = new PermissionGroup("Old Group");
		oldGroup.grant("P1", true, false, false);

		PermissionGroup newGroup = new PermissionGroup("New Group");
		newGroup.grant("P2", true, true, false);

		Personnel personnel = new Personnel("user@example.com", "hash", "John", "Doe", oldGroup);
		ReflectionTestUtils.setField(personnel, "id", 5L);

		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(2L);

		when(personnelRepository.findById(5L)).thenReturn(Optional.of(personnel));
		when(permissionGroupRepository.findActiveByIdWithEntries(2L)).thenReturn(Optional.of(newGroup));
		when(personnelRepository.save(any(Personnel.class))).thenReturn(personnel);

		PersonnelListResponse response = service.reassignPermissionGroup(5L, request);

		assertThat(response.email()).isEqualTo("user@example.com");
		verify(activityLogClient).log(any(), any(), any(), any());
	}

	@Test
	void reassignPermissionGroupThrowsWhenPersonnelNotFound() {
		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(1L);

		when(personnelRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.reassignPermissionGroup(999L, request))
				.isInstanceOf(PersonnelNotFoundException.class);
	}

	@Test
	void reassignPermissionGroupThrowsWhenBootstrapAdmin() {
		PermissionGroup group = new PermissionGroup("Admin");
		Personnel bootstrap = new Personnel("super@example.com", "hash", "Super", "Admin", group);
		ReflectionTestUtils.setField(bootstrap, "id", 1L);

		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(2L);

		when(personnelRepository.findById(1L)).thenReturn(Optional.of(bootstrap));

		assertThatThrownBy(() -> service.reassignPermissionGroup(1L, request))
				.isInstanceOf(CannotRemoveLastP0AdminException.class)
				.hasMessage("Cannot remove the last personnel account with P0 (Permissions) access - " +
						"at least one P0 admin must exist to manage the system");
	}

	@Test
	void reassignPermissionGroupThrowsWhenRemovingLastP0Admin() {
		PermissionGroup p0Group = new PermissionGroup("P0 Group");
		p0Group.grant("P0", true, true, true);

		PermissionGroup nonP0Group = new PermissionGroup("Non-P0 Group");
		nonP0Group.grant("P1", true, false, false);

		Personnel personnel = new Personnel("user@example.com", "hash", "John", "Doe", p0Group);
		ReflectionTestUtils.setField(personnel, "id", 5L);

		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(2L);

		when(personnelRepository.findById(5L)).thenReturn(Optional.of(personnel));
		when(permissionGroupRepository.findActiveByIdWithEntries(2L)).thenReturn(Optional.of(nonP0Group));
		when(personnelRepository.countWithP0Access()).thenReturn(1L);

		assertThatThrownBy(() -> service.reassignPermissionGroup(5L, request))
				.isInstanceOf(CannotRemoveLastP0AdminException.class);

		verify(personnelRepository, never()).save(any());
	}

	@Test
	void reassignPermissionGroupSucceedsWhenRemovingP0WithMultipleAdmins() {
		PermissionGroup p0Group = new PermissionGroup("P0 Group");
		p0Group.grant("P0", true, true, true);

		PermissionGroup nonP0Group = new PermissionGroup("Non-P0 Group");
		nonP0Group.grant("P1", true, false, false);

		Personnel personnel = new Personnel("user@example.com", "hash", "John", "Doe", p0Group);
		ReflectionTestUtils.setField(personnel, "id", 5L);

		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(2L);

		when(personnelRepository.findById(5L)).thenReturn(Optional.of(personnel));
		when(permissionGroupRepository.findActiveByIdWithEntries(2L)).thenReturn(Optional.of(nonP0Group));
		when(personnelRepository.countWithP0Access()).thenReturn(2L);
		when(personnelRepository.save(any(Personnel.class))).thenReturn(personnel);

		PersonnelListResponse response = service.reassignPermissionGroup(5L, request);

		assertThat(response.email()).isEqualTo("user@example.com");
		verify(personnelRepository).save(any(Personnel.class));
	}
}
