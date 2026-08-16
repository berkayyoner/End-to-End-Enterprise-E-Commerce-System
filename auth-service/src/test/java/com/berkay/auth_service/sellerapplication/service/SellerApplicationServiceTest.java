package com.berkay.auth_service.sellerapplication.service;

import com.berkay.auth_service.exception.AlreadySellerException;
import com.berkay.auth_service.exception.DuplicatePendingSellerApplicationException;
import com.berkay.auth_service.exception.IdNotVerifiedException;
import com.berkay.auth_service.exception.SellerApplicationAlreadyReviewedException;
import com.berkay.auth_service.sellerapplication.dto.SellerApplicationRequest;
import com.berkay.auth_service.sellerapplication.dto.SellerApplicationResponse;
import com.berkay.auth_service.sellerapplication.entity.SellerApplication;
import com.berkay.auth_service.sellerapplication.entity.SellerApplicationStatus;
import com.berkay.auth_service.sellerapplication.repository.SellerApplicationRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerApplicationServiceTest {

	@Mock
	private SellerApplicationRepository applicationRepository;

	@Mock
	private AppUserRepository appUserRepository;

	private SellerApplicationService service;

	@BeforeEach
	void setUp() {
		service = new SellerApplicationService(applicationRepository, appUserRepository);
	}

	private static SellerApplicationRequest request() {
		return new SellerApplicationRequest("Acme LLC", "1234567890", "+90 555 555 5555", "Istanbul, Turkiye");
	}

	@Test
	void rejectsApplicationWhenNotIdVerified() {
		AppUser user = new AppUser("alice@example.com", "hash", "Alice", "Smith", null);
		when(appUserRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> service.submit("alice@example.com", request()))
				.isInstanceOf(IdNotVerifiedException.class);
	}

	@Test
	void rejectsApplicationWhenAlreadySeller() {
		AppUser user = new AppUser("bob@example.com", "hash", "Bob", "Jones", null);
		user.markIdVerified();
		user.approveAsSeller();
		when(appUserRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> service.submit("bob@example.com", request()))
				.isInstanceOf(AlreadySellerException.class);
	}

	@Test
	void rejectsApplicationWhenOneIsAlreadyPending() {
		AppUser user = new AppUser("carol@example.com", "hash", "Carol", "Lee", null);
		user.markIdVerified();
		when(appUserRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user));
		when(applicationRepository.existsByAppUserAndStatus(user, SellerApplicationStatus.PENDING)).thenReturn(true);

		assertThatThrownBy(() -> service.submit("carol@example.com", request()))
				.isInstanceOf(DuplicatePendingSellerApplicationException.class);
	}

	@Test
	void approvingMarksTheApplicantASeller() {
		AppUser user = new AppUser("dave@example.com", "hash", "Dave", "Kim", null);
		user.markIdVerified();
		SellerApplication application = new SellerApplication(user, "Acme LLC", "1234567890", "+90 555 555 5555",
				"Istanbul, Turkiye");
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
		when(applicationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		SellerApplicationResponse response = service.approve(1L, "moderator@berkay.local");

		assertThat(response.status()).isEqualTo(SellerApplicationStatus.APPROVED);
		assertThat(user.isSeller()).isTrue();
	}

	@Test
	void cannotReviewAnAlreadyReviewedApplication() {
		AppUser user = new AppUser("erin@example.com", "hash", "Erin", "Fox", null);
		SellerApplication application = new SellerApplication(user, "Acme LLC", "1234567890", "+90 555 555 5555",
				"Istanbul, Turkiye");
		application.reject("moderator@berkay.local", "incomplete");
		when(applicationRepository.findById(2L)).thenReturn(Optional.of(application));

		assertThatThrownBy(() -> service.approve(2L, "moderator@berkay.local"))
				.isInstanceOf(SellerApplicationAlreadyReviewedException.class);
	}
}
