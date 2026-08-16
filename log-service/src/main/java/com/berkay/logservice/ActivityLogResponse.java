package com.berkay.logservice;

import java.time.Instant;

public record ActivityLogResponse(
		Long id,
		ActorType actorType,
		Long actorId,
		String sourceService,
		String action,
		String details,
		String ipAddress,
		Instant occurredAt) {

	static ActivityLogResponse from(ActivityLog log) {
		return new ActivityLogResponse(log.getId(), log.getActorType(), log.getActorId(), log.getSourceService(),
				log.getAction(), log.getDetails(), log.getIpAddress(), log.getOccurredAt());
	}
}
