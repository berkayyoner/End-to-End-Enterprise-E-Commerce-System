package com.berkay.order_service.payment.repository;

import com.berkay.order_service.payment.entity.SavedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedCardRepository extends JpaRepository<SavedCard, Long> {
	Optional<SavedCard> findByBuyerIdAndDeletedFalse(String buyerId);
}
