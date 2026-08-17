package com.berkay.auth_service.personnel.controller;

import com.berkay.auth_service.exception.PersonnelNotFoundException;
import com.berkay.auth_service.personnel.dto.PersonnelResponse;
import com.berkay.auth_service.personnel.entity.PermissionEntry;
import com.berkay.auth_service.personnel.entity.PermissionGroup;
import com.berkay.auth_service.personnel.entity.Personnel;
import com.berkay.auth_service.personnel.repository.PersonnelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonnelAccountControllerTest {

	@Mock
	private PersonnelRepository personnelRepository;

	@Mock
	private Authentication authentication;

	@Test
	void meReturnsCurrentPersonnelWithPermissions() {
		PersonnelAccountController controller = new PersonnelAccountController(personnelRepository);

		PermissionGroup group = new PermissionGroup("Admin");
		group.grant("P0", true, true, true);
		group.grant("P1", true, false, false);

		Personnel personnel = new Personnel("admin@example.com", "hashed-pwd", "John", "Doe", group);

		when(authentication.getName()).thenReturn("admin@example.com");
		when(personnelRepository.findByEmailWithPermissionsAndNotDeleted("admin@example.com")).thenReturn(Optional.of(personnel));

		PersonnelResponse response = controller.me(authentication);

		assertThat(response.email()).isEqualTo("admin@example.com");
		assertThat(response.firstName()).isEqualTo("John");
		assertThat(response.lastName()).isEqualTo("Doe");
		assertThat(response.permissions()).containsExactlyInAnyOrder("P0AED", "P1A");
	}

	@Test
	void meThrowsWhenPersonnelNotFound() {
		PersonnelAccountController controller = new PersonnelAccountController(personnelRepository);

		when(authentication.getName()).thenReturn("missing@example.com");
		when(personnelRepository.findByEmailWithPermissionsAndNotDeleted("missing@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> controller.me(authentication))
				.isInstanceOf(PersonnelNotFoundException.class)
				.hasMessage("Personnel not found");
	}
}
