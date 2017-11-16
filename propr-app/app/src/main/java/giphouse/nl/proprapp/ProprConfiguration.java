package giphouse.nl.proprapp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haye
 */
public class ProprConfiguration {

	private final String clientId;

	private final String clientSecret;

	private final String backendUrl;

	public ProprConfiguration(final String clientId, final String clientSecret, final String backendUrl) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.backendUrl = backendUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getBackendUrl() {
		return backendUrl;
	}
}
