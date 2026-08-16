package com.berkay.auth_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "berkay.oauth2.public-client")
public class OAuth2ClientProperties {

	private List<String> redirectUris = List.of();

	public List<String> getRedirectUris() {
		return redirectUris;
	}

	public void setRedirectUris(List<String> redirectUris) {
		this.redirectUris = redirectUris;
	}
}
