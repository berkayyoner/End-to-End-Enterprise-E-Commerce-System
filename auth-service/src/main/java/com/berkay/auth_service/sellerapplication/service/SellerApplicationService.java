package com.berkay.auth_service.sellerapplication.service;

import com.berkay.auth_service.activitylog.ActivityLogClient;
import com.berkay.auth_service.activitylog.ActorType;
import com.berkay.auth_service.exception.AlreadySellerException;
import com.berkay.auth_service.exception.DuplicatePendingSellerApplicationException;
import com.berkay.auth_service.exception.IdNotVerifiedException;
import com.berkay.auth_service.exception.SellerApplicationAlreadyReviewedException;
import com.berkay.auth_service.exception.SellerApplicationNotFoundException;
import com.berkay.auth_service.sellerapplication.dto.SellerApplicationRequest;
import com.berkay.auth_service.sellerapplication.dto.SellerApplicationResponse;
import com.berkay.auth_service.sellerapplication.entity.SellerApplication;
import com.berkay.auth_service.sellerapplication.entity.SellerApplicationStatus;
import com.berkay.auth_service.sellerapplication.repository.SellerApplicationRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RULES.md's dummy "apply to become a Seller" flow - no company info is actually checked
 * against anything real, and applying requires the account to already be ID-verified (task
 * 1.4), matching RULES.md's "Sell on Berkay" button gate.
 */
@Service
public class SellerApplicationService {

	private final SellerApplicationRepository applicationRepository;
	private final AppUserRepository appUserRepository;
	private final ActivityLogClient activityLogClient;

	public SellerApplicationService(SellerApplicationRepository applicationRepository,
			AppUserRepository appUserRepository, ActivityLogClient activityLogClient) {
		this.applicationRepository = applicationRepository;
		this.appUserRepository = appUserRepository;
		this.activityLogClient = activityLogClient;
	}

	@Transactional
	public SellerApplicationResponse submit(String appUserEmail, SellerApplicationRequest request) {
		AppUser appUser = appUserRepository.findByEmail(appUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("No account for email: " + appUserEmail));

		if (!appUser.isIdVerified()) {
			throw new IdNotVerifiedException();
		}
		if (appUser.isSeller()) {
			throw new AlreadySellerException();
		}
		if (applicationRepository.existsByAppUserAndStatus(appUser, SellerApplicationStatus.PENDING)) {
			throw new DuplicatePendingSellerApplicationException();
		}

		SellerApplication application = new SellerApplication(
				appUser,
				request.companyName().trim(),
				request.taxId().trim(),
				request.companyPhone().trim(),
				request.companyAddress().trim());

		SellerApplication saved = applicationRepository.save(application);
		activityLogClient.log(ActorType.USER, appUser.getId(), "SELLER_APPLICATION_SUBMITTED",
				"applicationId=" + saved.getId() + ", companyName=" + saved.getCompanyName());

		return SellerApplicationResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public Page<SellerApplicationResponse> list(SellerApplicationStatus status, Pageable pageable) {
		return applicationRepository.findAllByStatus(status, pageable).map(SellerApplicationResponse::from);
	}

	@Transactional
	public SellerApplicationResponse approve(Long applicationId, String reviewerEmail) {
		SellerApplication application = findPendingOrThrow(applicationId);
		application.approve(reviewerEmail);
		application.getAppUser().approveAsSeller();
		SellerApplicationResponse response = SellerApplicationResponse.from(applicationRepository.save(application));
		activityLogClient.log(ActorType.PERSONNEL, null, "SELLER_APPLICATION_APPROVED",
				"applicationId=" + applicationId + ", reviewer=" + reviewerEmail + ", appUser=" + response.appUserEmail());
		return response;
	}

	@Transactional
	public SellerApplicationResponse reject(Long applicationId, String reviewerEmail, String reason) {
		SellerApplication application = findPendingOrThrow(applicationId);
		application.reject(reviewerEmail, reason);
		SellerApplicationResponse response = SellerApplicationResponse.from(applicationRepository.save(application));
		activityLogClient.log(ActorType.PERSONNEL, null, "SELLER_APPLICATION_REJECTED",
				"applicationId=" + applicationId + ", reviewer=" + reviewerEmail + ", reason=" + reason);
		return response;
	}

	private SellerApplication findPendingOrThrow(Long applicationId) {
		SellerApplication application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new SellerApplicationNotFoundException(applicationId));
		if (!application.isPending()) {
			throw new SellerApplicationAlreadyReviewedException(applicationId);
		}
		return application;
	}
}
