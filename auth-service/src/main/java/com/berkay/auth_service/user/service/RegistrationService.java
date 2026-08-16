package com.berkay.auth_service.user.service;

import com.berkay.auth_service.exception.EmailAlreadyRegisteredException;
import com.berkay.auth_service.user.dto.RegisterRequest;
import com.berkay.auth_service.user.dto.RegisterResponse;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
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

		return RegisterResponse.from(appUserRepository.save(user));
	}
}
