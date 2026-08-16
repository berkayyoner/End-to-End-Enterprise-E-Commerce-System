package com.berkay.auth_service.ban.repository;

import com.berkay.auth_service.ban.entity.BannedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {

	boolean existsByOriginalAppUserId(Long originalAppUserId);
}
