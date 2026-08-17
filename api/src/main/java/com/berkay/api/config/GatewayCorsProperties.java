package com.berkay.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "berkay.cors")
public class GatewayCorsProperties {

	private List<String> publicAllowedOrigins = List.of();
	private List<String> personnelAllowedOrigins = List.of();

	public List<String> getPublicAllowedOrigins() {
		return publicAllowedOrigins;
	}

	public void setPublicAllowedOrigins(List<String> publicAllowedOrigins) {
		this.publicAllowedOrigins = publicAllowedOrigins;
	}

	public List<String> getPersonnelAllowedOrigins() {
		return personnelAllowedOrigins;
	}

	public void setPersonnelAllowedOrigins(List<String> personnelAllowedOrigins) {
		this.personnelAllowedOrigins = personnelAllowedOrigins;
	}
}
