package com.berkay.auth_service.idverification.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.exception.AlreadyIdVerifiedException;
import com.berkay.auth_service.exception.DuplicatePendingIdVerificationException;
import com.berkay.auth_service.exception.IdVerificationAlreadyReviewedException;
import com.berkay.auth_service.exception.InvalidIdVerificationSubmissionException;
import com.berkay.auth_service.idverification.dto.IdVerificationResponse;
import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.entity.IdVerificationStatus;
import com.berkay.auth_service.idverification.repository.IdVerificationApplicationRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdVerificationServiceTest {

	@Mock
	private IdVerificationApplicationRepository applicationRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private ActivityLogClient activityLogClient;

	private IdVerificationService service;

	@BeforeEach
	void setUp() {
		service = new IdVerificationService(applicationRepository, appUserRepository, activityLogClient);
	}

	@Test
	void rejectsSubmissionWhenAlreadyVerified() {
		AppUser user = new AppUser("alice@example.com", "hash", "Alice", "Smith", null);
		user.markIdVerified();
		when(appUserRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> service.submit("alice@example.com", "12345", jpeg(), jpeg()))
				.isInstanceOf(AlreadyIdVerifiedException.class);
	}

	@Test
	void rejectsSubmissionWhenAPendingApplicationAlreadyExists() {
		AppUser user = new AppUser("bob@example.com", "hash", "Bob", "Jones", null);
		when(appUserRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));
		when(applicationRepository.existsByAppUserAndStatus(user, IdVerificationStatus.PENDING)).thenReturn(true);

		assertThatThrownBy(() -> service.submit("bob@example.com", "12345", jpeg(), jpeg()))
				.isInstanceOf(DuplicatePendingIdVerificationException.class);
	}

	@Test
	void rejectsNonImagePhotos() {
		AppUser user = new AppUser("carol@example.com", "hash", "Carol", "Lee", null);
		when(appUserRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user));
		when(applicationRepository.existsByAppUserAndStatus(user, IdVerificationStatus.PENDING)).thenReturn(false);

		MockMultipartFile notAnImage = new MockMultipartFile("frontPhoto", "id.txt", "text/plain", "hello".getBytes());

		assertThatThrownBy(() -> service.submit("carol@example.com", "12345", notAnImage, jpeg()))
				.isInstanceOf(InvalidIdVerificationSubmissionException.class);
	}

	@Test
	void approvingMarksTheApplicantIdVerified() {
		AppUser user = new AppUser("dave@example.com", "hash", "Dave", "Kim", null);
		IdVerificationApplication application = new IdVerificationApplication(user, "12345",
				new byte[]{1}, "image/jpeg", new byte[]{2}, "image/jpeg");
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
		when(applicationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		IdVerificationResponse response = service.approve(1L, "moderator@berkay.local");

		assertThat(response.status()).isEqualTo(IdVerificationStatus.APPROVED);
		assertThat(user.isIdVerified()).isTrue();
	}

	@Test
	void cannotReviewAnAlreadyReviewedApplication() {
		AppUser user = new AppUser("erin@example.com", "hash", "Erin", "Fox", null);
		IdVerificationApplication application = new IdVerificationApplication(user, "12345",
				new byte[]{1}, "image/jpeg", new byte[]{2}, "image/jpeg");
		application.approve("moderator@berkay.local");
		when(applicationRepository.findById(2L)).thenReturn(Optional.of(application));

		assertThatThrownBy(() -> service.reject(2L, "moderator@berkay.local", "changed my mind"))
				.isInstanceOf(IdVerificationAlreadyReviewedException.class);
	}

	private static MockMultipartFile jpeg() {
		return new MockMultipartFile("photo", "id.jpg", "image/jpeg", new byte[]{1, 2, 3});
	}
}
