package com.berkay.auth_service.personnel.controller;

import com.berkay.auth_service.exception.CannotRemoveLastP0AdminException;
import com.berkay.auth_service.exception.DuplicatePersonnelEmailException;
import com.berkay.auth_service.personnel.dto.PermissionGroupAssignmentRequest;
import com.berkay.auth_service.personnel.dto.PersonnelCreateRequest;
import com.berkay.auth_service.personnel.dto.PersonnelListResponse;
import com.berkay.auth_service.personnel.service.PersonnelManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonnelManagementControllerTest {

	@Mock
	private PersonnelManagementService personnelManagementService;

	@Test
	void listReturnsAllPersonnel() {
		PersonnelManagementController controller = new PersonnelManagementController(personnelManagementService);

		PersonnelListResponse p1 = new PersonnelListResponse(1L, "admin@example.com", "John", "Doe", 1L, "Admin", Instant.now());
		PersonnelListResponse p2 = new PersonnelListResponse(2L, "mod@example.com", "Jane", "Smith", 2L, "Moderator", Instant.now());

		when(personnelManagementService.list()).thenReturn(List.of(p1, p2));

		List<PersonnelListResponse> responses = controller.list();

		assertThat(responses)
				.hasSize(2)
				.extracting(PersonnelListResponse::email)
				.containsExactly("admin@example.com", "mod@example.com");
	}

	@Test
	void createReturnsCreatedPersonnel() {
		PersonnelManagementController controller = new PersonnelManagementController(personnelManagementService);

		PersonnelCreateRequest request = new PersonnelCreateRequest(
				"newadmin@example.com",
				"password123",
				"John",
				"Doe",
				1L);

		PersonnelListResponse response = new PersonnelListResponse(10L, "newadmin@example.com", "John", "Doe", 1L, "Admin", Instant.now());

		when(personnelManagementService.create(request)).thenReturn(response);

		var result = controller.create(request);

		assertThat(result.getStatusCode().value()).isEqualTo(201);
		assertThat(result.getBody()).isEqualTo(response);
	}

	@Test
	void createThrowsDuplicateEmailException() {
		PersonnelManagementController controller = new PersonnelManagementController(personnelManagementService);

		PersonnelCreateRequest request = new PersonnelCreateRequest(
				"existing@example.com",
				"password123",
				"John",
				"Doe",
				1L);

		when(personnelManagementService.create(any())).thenThrow(new DuplicatePersonnelEmailException("existing@example.com"));

		assertThatThrownBy(() -> controller.create(request))
				.isInstanceOf(DuplicatePersonnelEmailException.class);
	}

	@Test
	void reassignPermissionGroupSucceeds() {
		PersonnelManagementController controller = new PersonnelManagementController(personnelManagementService);

		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(2L);
		PersonnelListResponse response = new PersonnelListResponse(5L, "user@example.com", "John", "Doe", 2L, "New Group", Instant.now());

		when(personnelManagementService.reassignPermissionGroup(5L, request)).thenReturn(response);

		var result = controller.reassignPermissionGroup(5L, request);

		assertThat(result.email()).isEqualTo("user@example.com");
		assertThat(result.permissionGroupId()).isEqualTo(2L);
	}

	@Test
	void reassignPermissionGroupThrowsCannotRemoveLastP0Admin() {
		PersonnelManagementController controller = new PersonnelManagementController(personnelManagementService);

		PermissionGroupAssignmentRequest request = new PermissionGroupAssignmentRequest(2L);

		when(personnelManagementService.reassignPermissionGroup(5L, request))
				.thenThrow(new CannotRemoveLastP0AdminException());

		assertThatThrownBy(() -> controller.reassignPermissionGroup(5L, request))
				.isInstanceOf(CannotRemoveLastP0AdminException.class);
	}
}
