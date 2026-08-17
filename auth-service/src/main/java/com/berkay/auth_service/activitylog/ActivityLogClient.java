package com.berkay.auth_service.activitylog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Task 1.7's "activity logging hooks": reports user/personnel actions to log-service (task 0.9)
 * over its Eureka-discovered service ID. Logging is best-effort by design - a log-service outage
 * must never fail the actual registration/login/review/etc. it's recording, so every failure is
 * caught and only warned about here.
 */
@Component
public class ActivityLogClient {

	private static final String SOURCE_SERVICE = "auth-service";

	private final Log log = LogFactory.getLog(getClass());
	private final RestClient restClient;

	public ActivityLogClient(@LoadBalanced RestClient.Builder loadBalancedRestClientBuilder) {
		this.restClient = loadBalancedRestClientBuilder.baseUrl("http://log-service").build();
	}

	public void log(ActorType actorType, Long actorId, String action, String details) {
		try {
			restClient.post()
					.uri("/logs")
					.body(new ActivityLogEntry(actorType, actorId, SOURCE_SERVICE, action, details, currentIpAddress()))
					.retrieve()
					.toBodilessEntity();
		} catch (Exception ex) {
			log.warn("Failed to record activity log [" + action + "]: " + ex.getMessage());
		}
	}

	private static String currentIpAddress() {
		if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
			return attributes.getRequest().getRemoteAddr();
		}
		return null;
	}
}
