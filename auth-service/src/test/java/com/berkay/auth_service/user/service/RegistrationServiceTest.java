package com.berkay.auth_service.user.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.exception.EmailAlreadyRegisteredException;
import com.berkay.auth_service.user.dto.RegisterRequest;
import com.berkay.auth_service.user.dto.RegisterResponse;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ActivityLogClient activityLogClient;

	private RegistrationService registrationService;

	@Test
	void registersANewUserWithNormalizedEmailAndHashedPassword() {
		registrationService = new RegistrationService(appUserRepository, passwordEncoder, activityLogClient);
		RegisterRequest request = new RegisterRequest(" Alice@Example.com ", "SuperSecret1", "Alice", "Smith", null);

		when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
		when(passwordEncoder.encode("SuperSecret1")).thenReturn("hashed-password");
		when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

		RegisterResponse response = registrationService.register(request);

		assertThat(response.email()).isEqualTo("alice@example.com");
		assertThat(response.firstName()).isEqualTo("Alice");
		assertThat(response.idVerified()).isFalse();
		assertThat(response.seller()).isFalse();

		ArgumentCaptor<AppUser> savedUser = ArgumentCaptor.forClass(AppUser.class);
		verify(appUserRepository).save(savedUser.capture());
		assertThat(savedUser.getValue().getPasswordHash()).isEqualTo("hashed-password");
	}

	@Test
	void rejectsRegistrationWhenEmailAlreadyExists() {
		registrationService = new RegistrationService(appUserRepository, passwordEncoder, activityLogClient);
		RegisterRequest request = new RegisterRequest("bob@example.com", "SuperSecret1", "Bob", "Jones", null);

		when(appUserRepository.existsByEmail("bob@example.com")).thenReturn(true);

		assertThatThrownBy(() -> registrationService.register(request))
				.isInstanceOf(EmailAlreadyRegisteredException.class);

		verify(appUserRepository, never()).save(any());
	}
}
