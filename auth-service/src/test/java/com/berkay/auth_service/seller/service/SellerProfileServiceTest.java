package com.berkay.auth_service.seller.service;

import com.berkay.auth_service.exception.AppUserNotFoundException;
import com.berkay.auth_service.seller.dto.SellerProfileResponse;
import com.berkay.auth_service.seller.dto.SellerPublicProfileResponse;
import com.berkay.auth_service.seller.dto.UpdateStoreNameRequest;
import com.berkay.auth_service.seller.entity.SellerEarnings;
import com.berkay.auth_service.seller.repository.SellerEarningsRepository;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerProfileServiceTest {

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private SellerEarningsRepository sellerEarningsRepository;

	private SellerProfileService sellerProfileService;

	@Test
	void getsSellerProfileWithEarningsAndFollowerCount() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser seller = new AppUser("seller@example.com", "hash", "Seller", "One", null);
		seller.approveAsSeller();
		seller.setStoreName("My Store");
		seller.setFollowerCount(42);

		SellerEarnings earnings = new SellerEarnings(1L);
		earnings.credit(new BigDecimal("199.99"));

		when(appUserRepository.findById(1L)).thenReturn(Optional.of(seller));
		when(sellerEarningsRepository.findBySellerId(1L)).thenReturn(Optional.of(earnings));

		SellerProfileResponse response = sellerProfileService.getSellerProfile(1L);

		assertThat(response.storeName()).isEqualTo("My Store");
		assertThat(response.totalEarned()).isEqualByComparingTo(new BigDecimal("199.99"));
		assertThat(response.followerCount()).isEqualTo(42);
		assertThat(response.email()).isEqualTo("seller@example.com");
	}

	@Test
	void createsEarningsRecordIfNotExists() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser seller = new AppUser("seller@example.com", "hash", "Seller", "One", null);
		seller.approveAsSeller();

		when(appUserRepository.findById(1L)).thenReturn(Optional.of(seller));
		when(sellerEarningsRepository.findBySellerId(1L)).thenReturn(Optional.empty());

		SellerEarnings newEarnings = new SellerEarnings(1L);
		when(sellerEarningsRepository.save(any(SellerEarnings.class))).thenReturn(newEarnings);

		SellerProfileResponse response = sellerProfileService.getSellerProfile(1L);

		assertThat(response.totalEarned()).isEqualByComparingTo(BigDecimal.ZERO);
		verify(sellerEarningsRepository).save(any(SellerEarnings.class));
	}

	@Test
	void updatesStoreName() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser seller = new AppUser("seller@example.com", "hash", "Seller", "One", null);
		seller.approveAsSeller();
		seller.setStoreName("Old Store Name");

		SellerEarnings earnings = new SellerEarnings(1L);
		earnings.credit(new BigDecimal("100.00"));

		when(appUserRepository.findById(1L)).thenReturn(Optional.of(seller));
		when(appUserRepository.save(any(AppUser.class))).thenReturn(seller);
		when(sellerEarningsRepository.findBySellerId(1L)).thenReturn(Optional.of(earnings));

		UpdateStoreNameRequest request = new UpdateStoreNameRequest("  New Store Name  ");
		SellerProfileResponse response = sellerProfileService.updateStoreName(1L, request);

		assertThat(response.storeName()).isEqualTo("New Store Name");
		assertThat(response.totalEarned()).isEqualByComparingTo(new BigDecimal("100.00"));
		verify(appUserRepository).save(any(AppUser.class));
	}

	@Test
	void getsPublicProfile() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser seller = new AppUser("seller@example.com", "hash", "Seller", "One", null);
		seller.approveAsSeller();
		seller.setStoreName("Public Store");
		seller.setFollowerCount(99);

		when(appUserRepository.findById(1L)).thenReturn(Optional.of(seller));

		SellerPublicProfileResponse response = sellerProfileService.getPublicProfile(1L);

		assertThat(response.storeName()).isEqualTo("Public Store");
		assertThat(response.followerCount()).isEqualTo(99);
	}

	@Test
	void rejectsGetProfileForNonSeller() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser customer = new AppUser("customer@example.com", "hash", "Customer", "One", null);
		// seller = false by default

		when(appUserRepository.findById(2L)).thenReturn(Optional.of(customer));

		assertThatThrownBy(() -> sellerProfileService.getSellerProfile(2L))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("User is not a seller");
	}

	@Test
	void rejectsUpdateStoreNameForNonSeller() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser customer = new AppUser("customer@example.com", "hash", "Customer", "One", null);

		when(appUserRepository.findById(2L)).thenReturn(Optional.of(customer));

		assertThatThrownBy(
				() -> sellerProfileService.updateStoreName(2L, new UpdateStoreNameRequest("Store")))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("User is not a seller");
	}

	@Test
	void rejectsPublicProfileForNonSeller() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);
		AppUser customer = new AppUser("customer@example.com", "hash", "Customer", "One", null);

		when(appUserRepository.findById(2L)).thenReturn(Optional.of(customer));

		assertThatThrownBy(() -> sellerProfileService.getPublicProfile(2L))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("User is not a seller");
	}

	@Test
	void throwsAppUserNotFoundExceptionWhenSellerDoesNotExist() {
		sellerProfileService = new SellerProfileService(appUserRepository, sellerEarningsRepository);

		when(appUserRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> sellerProfileService.getSellerProfile(999L))
				.isInstanceOf(AppUserNotFoundException.class);
	}
}
