package com.berkay.auth_service.seller.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerEarningsTest {

	@Test
	void creditsAmountToTotalEarned() {
		SellerEarnings earnings = new SellerEarnings(1L);
		assertThat(earnings.getTotalEarned()).isEqualByComparingTo(BigDecimal.ZERO);

		earnings.credit(new BigDecimal("50.00"));
		assertThat(earnings.getTotalEarned()).isEqualByComparingTo(new BigDecimal("50.00"));

		earnings.credit(new BigDecimal("25.75"));
		assertThat(earnings.getTotalEarned()).isEqualByComparingTo(new BigDecimal("75.75"));
	}

	@Test
	void creditsMultipleTimes() {
		SellerEarnings earnings = new SellerEarnings(1L);

		earnings.credit(new BigDecimal("100.00"));
		earnings.credit(new BigDecimal("50.00"));
		earnings.credit(new BigDecimal("25.00"));

		assertThat(earnings.getTotalEarned()).isEqualByComparingTo(new BigDecimal("175.00"));
	}

	@Test
	void rejectsNullAmount() {
		SellerEarnings earnings = new SellerEarnings(1L);

		assertThatThrownBy(() -> earnings.credit(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Credit amount must be non-null and non-negative");
	}

	@Test
	void rejectsNegativeAmount() {
		SellerEarnings earnings = new SellerEarnings(1L);

		assertThatThrownBy(() -> earnings.credit(new BigDecimal("-10.00")))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Credit amount must be non-null and non-negative");
	}

	@Test
	void acceptsZeroAmount() {
		SellerEarnings earnings = new SellerEarnings(1L);

		earnings.credit(BigDecimal.ZERO);

		assertThat(earnings.getTotalEarned()).isEqualByComparingTo(BigDecimal.ZERO);
	}
}
