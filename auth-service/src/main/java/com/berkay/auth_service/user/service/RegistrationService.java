package com.berkay.auth_service.user.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.activitylog.ActorType;
import com.berkay.auth_service.exception.EmailAlreadyRegisteredException;
import com.berkay.auth_service.user.dto.RegisterRequest;
import com.berkay.auth_service.user.dto.RegisterResponse;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final ActivityLogClient activityLogClient;

	public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
			ActivityLogClient activityLogClient) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.activityLogClient = activityLogClient;
	}

	@Transactional
	public RegisterResponse register(RegisterRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();
		if (appUserRepository.existsByEmail(normalizedEmail)) {
			throw new EmailAlreadyRegisteredException(normalizedEmail);
		}

		AppUser user = new AppUser(
				normalizedEmail,
				passwordEncoder.encode(request.password()),
				request.firstName().trim(),
				request.lastName().trim(),
				request.phoneNumber() != null ? request.phoneNumber().trim() : null);

		AppUser saved = appUserRepository.save(user);
		activityLogClient.log(ActorType.USER, saved.getId(), "USER_REGISTERED", "email=" + saved.getEmail());

		return RegisterResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public RegisterResponse findByEmail(String email) {
		return appUserRepository.findByEmail(email)
				.map(RegisterResponse::from)
				.orElseThrow(() -> new UsernameNotFoundException("No account for email: " + email));
	}
}
