package com.berkay.logservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	Page<ActivityLog> findByActorTypeAndActorId(ActorType actorType, Long actorId, Pageable pageable);

	Page<ActivityLog> findByActorType(ActorType actorType, Pageable pageable);

	Page<ActivityLog> findBySourceService(String sourceService, Pageable pageable);
}
