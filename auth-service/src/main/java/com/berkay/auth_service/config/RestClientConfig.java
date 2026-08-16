package com.berkay.auth_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	/**
	 * Boot's own RestClientAutoConfiguration only creates its default RestClient.Builder bean
	 * when none already exists (@ConditionalOnMissingBean), so defining the @LoadBalanced one
	 * below without this one would leave it as the sole RestClient.Builder in the context -
	 * Eureka's own transport client then autowires it unqualified and (wrongly) routes its
	 * registration/heartbeat calls through the load balancer instead of straight to the
	 * configured Eureka server URL. This bean restores a second, unqualified candidate; @Primary
	 * lets Eureka's ObjectProvider<RestClient.Builder>-based lookup resolve it unambiguously
	 * instead of throwing NoUniqueBeanDefinitionException, while explicit @LoadBalanced
	 * injection points (below, and ActivityLogClient) still get the load-balanced one.
	 */
	@Bean
	@Primary
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	/**
	 * A Eureka-aware builder (task 1.7) so callers can resolve other services by their
	 * spring.application.name (e.g. "log-service") instead of a hardcoded host:port.
	 */
	@Bean
	@LoadBalanced
	public RestClient.Builder loadBalancedRestClientBuilder() {
		return RestClient.builder();
	}
}
