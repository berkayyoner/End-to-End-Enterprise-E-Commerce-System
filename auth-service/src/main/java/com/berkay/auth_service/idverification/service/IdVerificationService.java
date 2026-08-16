package com.berkay.auth_service.idverification.service;

import com.berkay.auth_service.exception.AlreadyIdVerifiedException;
import com.berkay.auth_service.exception.DuplicatePendingIdVerificationException;
import com.berkay.auth_service.exception.IdVerificationAlreadyReviewedException;
import com.berkay.auth_service.exception.IdVerificationApplicationNotFoundException;
import com.berkay.auth_service.exception.InvalidIdVerificationSubmissionException;
import com.berkay.auth_service.idverification.dto.IdVerificationResponse;
import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.entity.IdVerificationStatus;
import com.berkay.auth_service.idverification.repository.IdVerificationApplicationRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * RULES.md's dummy ID verification flow. Nothing here validates the ID number or inspects the
 * photos - it exists purely to gate purchases/seller applications behind a submit-then-review
 * workflow, per RULES.md.
 */
@Service
public class IdVerificationService {

	private static final long MAX_PHOTO_BYTES = 5L * 1024 * 1024;

	private final IdVerificationApplicationRepository applicationRepository;
	private final AppUserRepository appUserRepository;

	public IdVerificationService(IdVerificationApplicationRepository applicationRepository,
			AppUserRepository appUserRepository) {
		this.applicationRepository = applicationRepository;
		this.appUserRepository = appUserRepository;
	}

	@Transactional
	public IdVerificationResponse submit(String appUserEmail, String idNumber, MultipartFile frontPhoto,
			MultipartFile backPhoto) {
		AppUser appUser = appUserRepository.findByEmail(appUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("No account for email: " + appUserEmail));

		if (appUser.isIdVerified()) {
			throw new AlreadyIdVerifiedException();
		}
		if (applicationRepository.existsByAppUserAndStatus(appUser, IdVerificationStatus.PENDING)) {
			throw new DuplicatePendingIdVerificationException();
		}
		if (idNumber == null || idNumber.isBlank()) {
			throw new InvalidIdVerificationSubmissionException("idNumber must not be blank");
		}
		validatePhoto(frontPhoto, "frontPhoto");
		validatePhoto(backPhoto, "backPhoto");

		IdVerificationApplication application = new IdVerificationApplication(
				appUser,
				idNumber.trim(),
				readBytes(frontPhoto),
				frontPhoto.getContentType(),
				readBytes(backPhoto),
				backPhoto.getContentType());

		return IdVerificationResponse.from(applicationRepository.save(application));
	}

	@Transactional(readOnly = true)
	public Page<IdVerificationResponse> list(IdVerificationStatus status, Pageable pageable) {
		return applicationRepository.findAllByStatus(status, pageable).map(IdVerificationResponse::from);
	}

	@Transactional
	public IdVerificationResponse approve(Long applicationId, String reviewerEmail) {
		IdVerificationApplication application = findPendingOrThrow(applicationId);
		application.approve(reviewerEmail);
		application.getAppUser().markIdVerified();
		return IdVerificationResponse.from(applicationRepository.save(application));
	}

	@Transactional
	public IdVerificationResponse reject(Long applicationId, String reviewerEmail, String reason) {
		IdVerificationApplication application = findPendingOrThrow(applicationId);
		application.reject(reviewerEmail, reason);
		return IdVerificationResponse.from(applicationRepository.save(application));
	}

	private IdVerificationApplication findPendingOrThrow(Long applicationId) {
		IdVerificationApplication application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new IdVerificationApplicationNotFoundException(applicationId));
		if (!application.isPending()) {
			throw new IdVerificationAlreadyReviewedException(applicationId);
		}
		return application;
	}

	private static void validatePhoto(MultipartFile photo, String fieldName) {
		if (photo == null || photo.isEmpty()) {
			throw new InvalidIdVerificationSubmissionException(fieldName + " must not be empty");
		}
		if (photo.getSize() > MAX_PHOTO_BYTES) {
			throw new InvalidIdVerificationSubmissionException(fieldName + " must be at most 5MB");
		}
		String contentType = photo.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new InvalidIdVerificationSubmissionException(fieldName + " must be an image");
		}
	}

	private static byte[] readBytes(MultipartFile file) {
		try {
			return file.getBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read " + file.getName(), e);
		}
	}
}
