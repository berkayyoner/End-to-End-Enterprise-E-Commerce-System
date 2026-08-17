package com.berkay.auth_service.ban.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.activitylog.ActorType;
import com.berkay.auth_service.ban.dto.BannedUserResponse;
import com.berkay.auth_service.ban.entity.BannedUser;
import com.berkay.auth_service.ban.repository.BannedUserRepository;
import com.berkay.auth_service.exception.AppUserNotFoundException;
import com.berkay.auth_service.exception.UserAlreadyBannedException;
import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.repository.IdVerificationApplicationRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

/**
 * RULES.md's "Users" page ban action: snapshots the account's identifying info into
 * banned_user (id numbers pulled from its ID verification history, task 1.4) and soft-deletes
 * the AppUser itself (BaseEntity#softDelete - never a hard delete) so
 * {@link com.berkay.auth_service.user.security.AppUserDetailsService} reports it as disabled
 * and it can no longer log in.
 */
@Service
public class BanService {

	private final BannedUserRepository bannedUserRepository;
	private final AppUserRepository appUserRepository;
	private final IdVerificationApplicationRepository idVerificationApplicationRepository;
	private final ActivityLogClient activityLogClient;

	public BanService(BannedUserRepository bannedUserRepository, AppUserRepository appUserRepository,
			IdVerificationApplicationRepository idVerificationApplicationRepository,
			ActivityLogClient activityLogClient) {
		this.bannedUserRepository = bannedUserRepository;
		this.appUserRepository = appUserRepository;
		this.idVerificationApplicationRepository = idVerificationApplicationRepository;
		this.activityLogClient = activityLogClient;
	}

	@Transactional
	public BannedUserResponse ban(Long appUserId, String reason) {
		AppUser appUser = appUserRepository.findById(appUserId)
				.orElseThrow(() -> new AppUserNotFoundException(appUserId));

		if (bannedUserRepository.existsByOriginalAppUserId(appUserId)) {
			throw new UserAlreadyBannedException();
		}

		String idNumbers = idVerificationApplicationRepository.findAllByAppUser(appUser).stream()
				.map(IdVerificationApplication::getIdNumber)
				.distinct()
				.sorted(Comparator.naturalOrder())
				.reduce((a, b) -> a + ", " + b)
				.orElse(null);

		// login IPs are now recorded in log-service (task 1.7) but this snapshot doesn't query
		// them back yet - a reasonable future enhancement, not required by RULES.md today.
		BannedUser bannedUser = new BannedUser(appUser.getId(), appUser.getEmail(), appUser.getPhoneNumber(),
				idNumbers, null, reason);
		bannedUserRepository.save(bannedUser);

		appUser.softDelete();
		appUserRepository.save(appUser);

		BannedUserResponse response = BannedUserResponse.from(bannedUser);
		activityLogClient.log(ActorType.PERSONNEL, null, "USER_BANNED",
				"bannedAppUserId=" + appUserId + ", bannedEmail=" + appUser.getEmail() + ", reason=" + reason);
		return response;
	}

	@Transactional(readOnly = true)
	public Page<BannedUserResponse> list(Pageable pageable) {
		return bannedUserRepository.findAll(pageable).map(BannedUserResponse::from);
	}
}
