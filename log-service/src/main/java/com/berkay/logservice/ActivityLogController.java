package com.berkay.logservice;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityLogController {

	private final ActivityLogService service;

	public ActivityLogController(ActivityLogService service) {
		this.service = service;
	}

	@PostMapping("/logs")
	public ResponseEntity<ActivityLogResponse> record(@Valid @RequestBody ActivityLogRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.record(request));
	}

	@GetMapping("/logs")
	public Page<ActivityLogResponse> search(
			@RequestParam(required = false) ActorType actorType,
			@RequestParam(required = false) Long actorId,
			@RequestParam(required = false) String sourceService,
			Pageable pageable) {
		return service.search(actorType, actorId, sourceService, pageable);
	}
}
