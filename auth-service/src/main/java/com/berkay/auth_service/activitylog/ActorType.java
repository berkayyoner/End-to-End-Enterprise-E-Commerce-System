package com.berkay.auth_service.activitylog;

/**
 * Mirrors log-service's own ActorType by contract (a plain JSON string, not a shared Java type -
 * log-service is a separate deployable, see CONVENTIONS.md).
 */
public enum ActorType {
	USER,
	PERSONNEL
}
