package giphouse.nl.proprapp;

import giphouse.nl.proprapp.account.Token;
import okhttp3.OkHttpClient;

/**
 * @author haye
 */
public class NetworkClientHolder {

	private static NetworkClientHolder networkClientHolder;

	private final OkHttpClient okHttpClient;

	private final Token token;

	private final ProprConfiguration configuration;

	private NetworkClientHolder(final OkHttpClient client, final ProprConfiguration proprConfiguration, final Token token) {
		this.okHttpClient = client;
		this.configuration = proprConfiguration;
		this.token = token;
	}

	public static NetworkClientHolder get() {
		return networkClientHolder;
	}

	static void init(final OkHttpClient client, final ProprConfiguration proprConfiguration, final Token token) {
		networkClientHolder = new NetworkClientHolder(client, proprConfiguration, token);
	}

	public OkHttpClient getOkHttpClient() {
		return okHttpClient;
	}

	public Token getToken() {
		return token;
	}

	public ProprConfiguration getConfiguration() {
		return configuration;
	}
}
