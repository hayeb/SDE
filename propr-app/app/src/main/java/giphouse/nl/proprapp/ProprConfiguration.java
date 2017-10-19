package giphouse.nl.proprapp;

/**
 * @author haye
 */
public class ProprConfiguration {

	private final String backendUrl;

	public static final String CLIENT_ID = "app";

	public static final String CLIENT_SECRET = "secret";

	ProprConfiguration(final String backendUrl) {
		this.backendUrl = backendUrl;
	}

	public String getBackendUrl() {
		return backendUrl;
	}
}
