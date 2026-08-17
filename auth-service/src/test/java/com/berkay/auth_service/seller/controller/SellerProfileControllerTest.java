package com.berkay.auth_service.seller.controller;

import com.berkay.auth_service.seller.dto.SellerProfileResponse;
import com.berkay.auth_service.seller.dto.SellerPublicProfileResponse;
import com.berkay.auth_service.seller.dto.UpdateStoreNameRequest;
import com.berkay.auth_service.seller.service.SellerProfileService;
import com.berkay.auth_service.user.entity.AppUser;
import com.berkay.auth_service.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerProfileControllerTest {

	@Mock
	private SellerProfileService sellerProfileService;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private Authentication authentication;

	private SellerProfileController controller;
	private AppUser seller;
	private AppUser customer;

	@BeforeEach
	void setUp() {
		controller = new SellerProfileController(sellerProfileService, appUserRepository);

		seller = new AppUser("seller@example.com", "hash", "Seller", "One", null);
		seller.approveAsSeller();
		seller.setStoreName("My Store");
		seller.setFollowerCount(42);

		customer = new AppUser("customer@example.com", "hash", "Customer", "One", null);
	}

	@Test
	void getSellerProfileReturnsProfileForSeller() {
		when(authentication.getName()).thenReturn("seller@example.com");
		when(appUserRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));
		SellerProfileResponse response = new SellerProfileResponse(
				null, "seller@example.com", "My Store", new BigDecimal("199.99"), 42);
		when(sellerProfileService.getSellerProfile(seller.getId())).thenReturn(response);

		ResponseEntity<SellerProfileResponse> result = controller.getMySellerProfile(authentication);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().storeName()).isEqualTo("My Store");
		assertThat(result.getBody().followerCount()).isEqualTo(42);
	}

	@Test
	void getSellerProfileReturnsForbiddenForNonSeller() {
		when(authentication.getName()).thenReturn("customer@example.com");
		when(appUserRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

		ResponseEntity<SellerProfileResponse> result = controller.getMySellerProfile(authentication);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(result.getBody()).isNull();
	}

	@Test
	void updateStoreNameSucceeds() {
		when(authentication.getName()).thenReturn("seller@example.com");
		when(appUserRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));
		UpdateStoreNameRequest request = new UpdateStoreNameRequest("New Store Name");
		SellerProfileResponse response = new SellerProfileResponse(
				null, "seller@example.com", "New Store Name", new BigDecimal("100.00"), 42);
		when(sellerProfileService.updateStoreName(seller.getId(), request)).thenReturn(response);

		ResponseEntity<SellerProfileResponse> result = controller.updateMyStoreName(authentication, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().storeName()).isEqualTo("New Store Name");
	}

	@Test
	void updateStoreNameReturnsForbiddenForNonSeller() {
		when(authentication.getName()).thenReturn("customer@example.com");
		when(appUserRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
		UpdateStoreNameRequest request = new UpdateStoreNameRequest("New Store Name");

		ResponseEntity<SellerProfileResponse> result = controller.updateMyStoreName(authentication, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(result.getBody()).isNull();
	}

	@Test
	void getPublicProfileReturnsSellerProfileWithoutEarnings() {
		SellerPublicProfileResponse response = new SellerPublicProfileResponse(seller.getId(), "My Store", 42);
		when(sellerProfileService.getPublicProfile(seller.getId())).thenReturn(response);

		ResponseEntity<SellerPublicProfileResponse> result = controller.getSellerPublicProfile(seller.getId());

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().storeName()).isEqualTo("My Store");
		assertThat(result.getBody().followerCount()).isEqualTo(42);
	}
}
