package giphouse.nl.proprapp.account;

import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprConfiguration;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author haye
 */
public class BackendAuthenticator {

	private static final String TAG = "BackendAuthenticator";

	private static final MediaType FORM_ENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private final ProprConfiguration proprConfiguration;

	private final OkHttpClient client;

	@Inject
	public BackendAuthenticator(final ProprConfiguration proprConfiguration, final OkHttpClient client) {
		this.proprConfiguration = proprConfiguration;
		this.client = client;
	}

	public Token signUp(final UserAccountDto accountDto) {
		// 1. Get initial token from backend
		final String clientToken = getInitialClientToken();
		if (StringUtils.isEmpty(clientToken)) {
			Log.d(TAG, "No token received from backend");
			return null;
		}

		// 2. Create user using initial token, and authenticate.
		final boolean userCreated = createUser(accountDto, clientToken);
		if (!userCreated) {
			return null;
		}

		// 3. Sign in, get user token and create account in account manager.
		return signIn(accountDto.getUsername(), accountDto.getPassword());
	}

	private String getInitialClientToken() {
		try {
			final Request request = new Request.Builder()
				.url(proprConfiguration.getBackendUrl() + "/oauth/token?grant_type=client_credentials")
				.post(RequestBody.create(FORM_ENCODED, ""))
				.header("Authorization", "Basic " + Base64.encodeToString((proprConfiguration.getClientId() + ":" + proprConfiguration.getClientSecret()).getBytes(), Base64.NO_WRAP))
				.build();

			final Response response = client.newCall(request).execute();

			final String responseString = getBodyString(response);

			if (!response.isSuccessful()) {
				Log.e(TAG, "Unable to get initial token from backend. Response: [" + response.code() + "] " + responseString);
				return null;
			}
			Log.d(TAG, "Got client token from backend");
			return new JSONObject(responseString).getString("access_token");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean createUser(final UserAccountDto accountDto, final String clientToken) {
		try {
			final JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", accountDto.getUsername());
			jsonObject.put("email", accountDto.getEmail());
			jsonObject.put("password", accountDto.getPassword());
			final Request request = new Request.Builder()
				.url(proprConfiguration.getBackendUrl() + "/api/users/register")
				.header("Authorization", "Bearer " + clientToken)
				.post(RequestBody.create(JSON, jsonObject.toString()))
				.build();
			final Response response = client.newCall(request).execute();

			if (!response.isSuccessful()) {
				Log.e(TAG, "Unable to register user with backend. Response: [" + response.code() + "] " + getBodyString(response));
				return false;

			}
			Log.d(TAG, "Created user " + accountDto.getUsername());
			return true;
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Token signIn(final String username, final String password) {
		final Request loginRequest = createLoginRequest(username, password);
		final Response loginResponse;
		try {
			loginResponse = client.newCall(loginRequest).execute();
		} catch (final IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Unable to make request to " + proprConfiguration.getBackendUrl());
			return null;
		}

		if (loginResponse.isSuccessful()) {
			return handleLoginResponse(loginResponse);
		}

		return null;
	}

	private Token handleLoginResponse(final Response response) {
		try {
			final JSONObject body = new JSONObject(getBodyString(response));
			return new Token(body.getString("access_token"), body.getString("refresh_token"));
		} catch (final JSONException e) {
			Log.e(TAG, "Unable to get body from response " + response);
		}
		return null;
	}

	private Request createLoginRequest(final String username, final String password) {
		final RequestBody body = new FormBody.Builder()
			.addEncoded("username", username)
			.addEncoded("password", password)
			.build();

		final String authorizationHeader = "Basic " + Base64.encodeToString((proprConfiguration.getClientId() + ":" + proprConfiguration.getClientSecret()).getBytes(), Base64.NO_WRAP);

		return new Request.Builder()
			.url(proprConfiguration.getBackendUrl() + "/oauth/token?grant_type=password")
			.header("Authorization", authorizationHeader)
			.post(body)
			.build();
	}

	private String getBodyString(final Response response) {
		final ResponseBody body = response.body();
		if (body == null) {
			return null;
		}
		try {
			return body.string();
		} catch (final IOException e) {
			Log.e(TAG, "Unable to get responsebody");
			return null;
		}
	}
}
