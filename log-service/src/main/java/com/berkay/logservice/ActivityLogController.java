package com.berkay.logservice;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

	// Permission gating lives directly on this public method via SpEL over the actorType
	// parameter. It must NOT be split into private helper methods annotated with their own
	// @PreAuthorize: Spring's method security is proxy-based AOP, which only intercepts calls
	// arriving through the bean's proxy (e.g. this controller method being invoked by
	// DispatcherServlet); a private method called via a plain `this.foo()` self-invocation
	// never passes through the proxy, so such a @PreAuthorize would be silently inert and the
	// endpoint would only be gated by "authenticated", not by the intended P8/P9 permission.
	@GetMapping("/logs")
	@PreAuthorize("(#actorType == T(com.berkay.logservice.ActorType).USER and hasAuthority('PERM_P8_VIEW')) "
			+ "or (#actorType == T(com.berkay.logservice.ActorType).PERSONNEL and hasAuthority('PERM_P9_VIEW')) "
			+ "or (#actorType != T(com.berkay.logservice.ActorType).USER and #actorType != T(com.berkay.logservice.ActorType).PERSONNEL "
			+ "and (hasAuthority('PERM_P8_VIEW') or hasAuthority('PERM_P9_VIEW')))")
	public Page<ActivityLogResponse> search(
			@RequestParam(required = false) ActorType actorType,
			@RequestParam(required = false) Long actorId,
			@RequestParam(required = false) String sourceService,
			Pageable pageable) {
		return service.search(actorType, actorId, sourceService, pageable);
	}
}
