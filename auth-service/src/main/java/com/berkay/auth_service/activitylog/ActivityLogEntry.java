package com.berkay.auth_service.activitylog;

record ActivityLogEntry(ActorType actorType, Long actorId, String sourceService, String action, String details,
		String ipAddress) {
}
