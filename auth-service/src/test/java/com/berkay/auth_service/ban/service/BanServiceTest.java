package com.berkay.auth_service.ban.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.ban.dto.BannedUserResponse;
import com.berkay.auth_service.ban.repository.BannedUserRepository;
import com.berkay.auth_service.exception.AppUserNotFoundException;
import com.berkay.auth_service.exception.UserAlreadyBannedException;
import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.repository.IdVerificationApplicationRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BanServiceTest {

	@Mock
	private BannedUserRepository bannedUserRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private IdVerificationApplicationRepository idVerificationApplicationRepository;

	@Mock
	private ActivityLogClient activityLogClient;

	private BanService service;

	@BeforeEach
	void setUp() {
		service = new BanService(bannedUserRepository, appUserRepository, idVerificationApplicationRepository,
				activityLogClient);
	}

	@Test
	void bansTheAccountAndSnapshotsItsIdNumbers() {
		AppUser user = new AppUser("alice@example.com", "hash", "Alice", "Smith", "+90 555 123 4567");
		when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
		when(bannedUserRepository.existsByOriginalAppUserId(1L)).thenReturn(false);

		IdVerificationApplication app1 = new IdVerificationApplication(user, "111", new byte[]{1}, "image/jpeg",
				new byte[]{2}, "image/jpeg");
		IdVerificationApplication app2 = new IdVerificationApplication(user, "222", new byte[]{1}, "image/jpeg",
				new byte[]{2}, "image/jpeg");
		when(idVerificationApplicationRepository.findAllByAppUser(user)).thenReturn(List.of(app1, app2));

		BannedUserResponse response = service.ban(1L, "repeated fraud");

		assertThat(response.email()).isEqualTo("alice@example.com");
		assertThat(response.phoneNumber()).isEqualTo("+90 555 123 4567");
		assertThat(response.idNumbers()).isEqualTo("111, 222");
		assertThat(response.reason()).isEqualTo("repeated fraud");
		assertThat(user.isDeleted()).isTrue();
	}

	@Test
	void bansAnAccountWithNoIdVerificationHistoryFine() {
		AppUser user = new AppUser("bob@example.com", "hash", "Bob", "Jones", null);
		when(appUserRepository.findById(2L)).thenReturn(Optional.of(user));
		when(bannedUserRepository.existsByOriginalAppUserId(2L)).thenReturn(false);
		when(idVerificationApplicationRepository.findAllByAppUser(user)).thenReturn(List.of());

		BannedUserResponse response = service.ban(2L, "spam");

		assertThat(response.idNumbers()).isNull();
		assertThat(response.ipAddress()).isNull();
	}

	@Test
	void rejectsBanningAnUnknownAccount() {
		when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.ban(99L, "reason"))
				.isInstanceOf(AppUserNotFoundException.class);
	}

	@Test
	void rejectsBanningAnAlreadyBannedAccount() {
		AppUser user = new AppUser("carol@example.com", "hash", "Carol", "Lee", null);
		when(appUserRepository.findById(3L)).thenReturn(Optional.of(user));
		when(bannedUserRepository.existsByOriginalAppUserId(3L)).thenReturn(true);

		assertThatThrownBy(() -> service.ban(3L, "reason"))
				.isInstanceOf(UserAlreadyBannedException.class);
	}
}
