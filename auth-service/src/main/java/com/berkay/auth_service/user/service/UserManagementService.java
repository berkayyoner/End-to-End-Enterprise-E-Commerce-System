package com.berkay.auth_service.user.service;

import com.berkay.auth_service.ban.repository.BannedUserRepository;
import com.berkay.auth_service.user.dto.AppUserEditRequest;
import com.berkay.auth_service.user.dto.AppUserListResponse;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import com.berkay.auth_service.exception.AppUserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Personnel-facing user management service (Phase 7.2): list all users, edit user details
 * (name/phone only), and soft-delete accounts. The existing ban flow (task 1.6) is separate
 * and remains in BanService.
 */
@Service
public class UserManagementService {

	private final AppUserRepository appUserRepository;
	private final BannedUserRepository bannedUserRepository;

	public UserManagementService(AppUserRepository appUserRepository, BannedUserRepository bannedUserRepository) {
		this.appUserRepository = appUserRepository;
		this.bannedUserRepository = bannedUserRepository;
	}

	@Transactional(readOnly = true)
	public Page<AppUserListResponse> listAllUsers(Pageable pageable) {
		return appUserRepository.findAllActive(pageable)
				.map(user -> AppUserListResponse.from(user, bannedUserRepository.existsByOriginalAppUserId(user.getId())));
	}

	@Transactional
	public AppUserListResponse editUser(Long userId, AppUserEditRequest request) {
		AppUser user = appUserRepository.findById(userId)
				.orElseThrow(() -> new AppUserNotFoundException(userId));

		// Only allow editing name and phone; email and password remain customer self-service
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setPhoneNumber(request.phoneNumber());

		appUserRepository.save(user);

		boolean banned = bannedUserRepository.existsByOriginalAppUserId(userId);
		return AppUserListResponse.from(user, banned);
	}

	@Transactional
	public void deleteUser(Long userId) {
		AppUser user = appUserRepository.findById(userId)
				.orElseThrow(() -> new AppUserNotFoundException(userId));

		user.softDelete();
		appUserRepository.save(user);
	}
}
