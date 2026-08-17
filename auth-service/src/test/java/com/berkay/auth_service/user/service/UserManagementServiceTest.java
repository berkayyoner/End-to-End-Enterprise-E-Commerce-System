package com.berkay.auth_service.user.service;

import com.berkay.auth_service.ban.repository.BannedUserRepository;
import com.berkay.auth_service.exception.AppUserNotFoundException;
import com.berkay.auth_service.user.dto.AppUserEditRequest;
import com.berkay.auth_service.user.dto.AppUserListResponse;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private BannedUserRepository bannedUserRepository;

	private UserManagementService service;

	@BeforeEach
	void setUp() {
		service = new UserManagementService(appUserRepository, bannedUserRepository);
	}

	@Test
	void listsAllActiveUsersWithBannedStatus() {
		AppUser user1 = new AppUser("alice@example.com", "hash", "Alice", "Smith", "+90 555 123 4567");
		AppUser user2 = new AppUser("bob@example.com", "hash", "Bob", "Jones", null);
		ReflectionTestUtils.setField(user1, "id", 1L);
		ReflectionTestUtils.setField(user2, "id", 2L);
		Pageable pageable = PageRequest.of(0, 20);
		Page<AppUser> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

		when(appUserRepository.findAllActive(pageable)).thenReturn(userPage);
		when(bannedUserRepository.existsByOriginalAppUserId(1L)).thenReturn(false);
		when(bannedUserRepository.existsByOriginalAppUserId(2L)).thenReturn(true);

		Page<AppUserListResponse> result = service.listAllUsers(pageable);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).banned()).isFalse();
		assertThat(result.getContent().get(1).banned()).isTrue();
	}

	@Test
	void editsUserNameAndPhone() {
		AppUser user = new AppUser("alice@example.com", "hash", "Alice", "Smith", "+90 555 123 4567");
		when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
		when(bannedUserRepository.existsByOriginalAppUserId(1L)).thenReturn(false);

		AppUserEditRequest request = new AppUserEditRequest("Alicia", "Smithson", "+90 555 999 8888");
		AppUserListResponse result = service.editUser(1L, request);

		assertThat(result.firstName()).isEqualTo("Alicia");
		assertThat(result.lastName()).isEqualTo("Smithson");
		assertThat(result.phoneNumber()).isEqualTo("+90 555 999 8888");
		verify(appUserRepository).save(user);
	}

	@Test
	void editRejectedForUnknownUser() {
		when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

		AppUserEditRequest request = new AppUserEditRequest("Name", "Lastname", "phone");
		assertThatThrownBy(() -> service.editUser(99L, request))
				.isInstanceOf(AppUserNotFoundException.class);
	}

	@Test
	void softDeletesUser() {
		AppUser user = new AppUser("alice@example.com", "hash", "Alice", "Smith", "+90 555 123 4567");
		when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

		service.deleteUser(1L);

		assertThat(user.isDeleted()).isTrue();
		verify(appUserRepository).save(user);
	}

	@Test
	void deleteRejectedForUnknownUser() {
		when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.deleteUser(99L))
				.isInstanceOf(AppUserNotFoundException.class);
	}
}
