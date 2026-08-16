package com.berkay.api.config;

import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Routes every downstream call through this gateway (RULES.md: front-ends never talk to
 * auth-service/product-service directly). Targets are resolved by service ID through Eureka
 * (registered via discovery-service) rather than hardcoded host:port, so instances can scale or
 * move without touching gateway config. Each route strips its own path prefix so the downstream
 * service sees a clean root-relative path.
 */
@Configuration
public class GatewayRoutesConfig {

	@Bean
	public RouterFunction<ServerResponse> authServiceRoute() {
		return GatewayRouterFunctions.route("auth-service")
				.route(RequestPredicates.path("/api/auth/**"), HandlerFunctions.http())
				.filter(FilterFunctions.stripPrefix(2))
				.filter(LoadBalancerFilterFunctions.lb("auth-service"))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> productServiceRoute() {
		return GatewayRouterFunctions.route("product-service")
				.route(RequestPredicates.path("/api/products/**"), HandlerFunctions.http())
				.filter(FilterFunctions.stripPrefix(2))
				.filter(LoadBalancerFilterFunctions.lb("product-service"))
				.build();
	}
}
