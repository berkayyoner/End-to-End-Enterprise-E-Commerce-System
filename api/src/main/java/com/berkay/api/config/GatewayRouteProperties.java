package com.berkay.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "berkay.gateway")
public class GatewayRouteProperties {

	private String authServiceUri;
	private String productServiceUri;

	public String getAuthServiceUri() {
		return authServiceUri;
	}

	public void setAuthServiceUri(String authServiceUri) {
		this.authServiceUri = authServiceUri;
	}

	public String getProductServiceUri() {
		return productServiceUri;
	}

	public void setProductServiceUri(String productServiceUri) {
		this.productServiceUri = productServiceUri;
	}
}
