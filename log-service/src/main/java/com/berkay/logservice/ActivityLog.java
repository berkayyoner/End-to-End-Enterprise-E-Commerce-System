package com.berkay.logservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A single immutable activity record. Unlike business entities elsewhere in the platform,
 * log rows are never soft-deleted or updated after being written - they are the audit trail
 * itself, so common-lib's mutable BaseEntity does not apply here.
 */
@Entity
@Table(name = "activity_log")
public class ActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "actor_type", nullable = false, updatable = false, length = 20)
	private ActorType actorType;

	@Column(name = "actor_id", updatable = false)
	private Long actorId;

	@Column(name = "source_service", nullable = false, updatable = false, length = 60)
	private String sourceService;

	@Column(name = "action", nullable = false, updatable = false, length = 100)
	private String action;

	@Column(name = "details", updatable = false, length = 2000)
	private String details;

	@Column(name = "ip_address", updatable = false, length = 64)
	private String ipAddress;

	@Column(name = "occurred_at", nullable = false, updatable = false)
	private Instant occurredAt;

	protected ActivityLog() {
	}

	public ActivityLog(ActorType actorType, Long actorId, String sourceService, String action, String details,
			String ipAddress, Instant occurredAt) {
		this.actorType = actorType;
		this.actorId = actorId;
		this.sourceService = sourceService;
		this.action = action;
		this.details = details;
		this.ipAddress = ipAddress;
		this.occurredAt = occurredAt;
	}

	public Long getId() {
		return id;
	}

	public ActorType getActorType() {
		return actorType;
	}

	public Long getActorId() {
		return actorId;
	}

	public String getSourceService() {
		return sourceService;
	}

	public String getAction() {
		return action;
	}

	public String getDetails() {
		return details;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}
}
