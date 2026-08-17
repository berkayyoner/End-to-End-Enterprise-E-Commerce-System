package com.berkay.logservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ActivityLogService {

	private final ActivityLogRepository repository;

	public ActivityLogService(ActivityLogRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public ActivityLogResponse record(ActivityLogRequest request) {
		ActivityLog log = new ActivityLog(request.actorType(), request.actorId(), request.sourceService(),
				request.action(), request.details(), request.ipAddress(), Instant.now());
		return ActivityLogResponse.from(repository.save(log));
	}

	@Transactional(readOnly = true)
	public Page<ActivityLogResponse> search(ActorType actorType, Long actorId, String sourceService, Pageable pageable) {
		Page<ActivityLog> page;
		if (actorType != null && actorId != null) {
			page = repository.findByActorTypeAndActorId(actorType, actorId, pageable);
		} else if (actorType != null) {
			page = repository.findByActorType(actorType, pageable);
		} else if (sourceService != null && !sourceService.isBlank()) {
			page = repository.findBySourceService(sourceService, pageable);
		} else {
			page = repository.findAll(pageable);
		}
		return page.map(ActivityLogResponse::from);
	}
}
