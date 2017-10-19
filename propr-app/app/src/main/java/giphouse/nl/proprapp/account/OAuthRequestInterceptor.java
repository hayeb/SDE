package giphouse.nl.proprapp.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import giphouse.nl.proprapp.NetworkClientHolder;
import giphouse.nl.proprapp.ProprConfiguration;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author haye
 */
public class OAuthRequestInterceptor implements Interceptor {

	private static final String TAG = "OAuthRequestInterceptor";

	private static final MediaType FORM_ENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

	private final AccountManager accountManager;

	public OAuthRequestInterceptor(final AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Override
	public Response intercept(final Chain chain) throws IOException {
		Request request = chain.request();

		final NetworkClientHolder nch = NetworkClientHolder.get();

		//Build new request
		final Request.Builder builder = request.newBuilder();

		final String token = nch.getToken().getAuthToken();
		setAuthHeader(builder, token);

		request = builder.build();
		final Response response = chain.proceed(request);

		if (response.code() != 401) {
			Log.i(TAG, "Token still fresh");
			return response;
		}
		final String currentToken = nch.getToken().getAuthToken();

		if (currentToken != null && currentToken.equals(token)) {

			if (!refreshToken()) {
				Log.e(TAG, "Refreshing token failed, returning original response");
				return response;
			}
		}

		if (nch.getToken().getAuthToken() != null) { //retry requires new auth token,
			setAuthHeader(builder, nch.getToken().getAuthToken()); //set auth token to updated
			request = builder.build();
			return chain.proceed(request); //repeat request with new token
		}

		return response;
	}

	private void setAuthHeader(final Request.Builder builder, final String token) {
		if (token != null) {
			builder.header("Authorization", String.format("Bearer %s", token));
		}
	}

	private boolean refreshToken() {
		final Request request = buildRefreshTokenRequest();

		synchronized (NetworkClientHolder.get().getOkHttpClient()) {
			final JSONObject parsedResponse = getRefreshTokenResponse(request);

			if (parsedResponse == null) {
				Log.e(TAG, "Unable to get response for refresh token request");
				return false;
			}

			if (!parsedResponse.has("access_token") || !parsedResponse.has("refresh_token")) {
				Log.e(TAG, "Response does not have required fields \"acces_token\" or \"refresh_token\"");
				return false;
			}

			String authToken = null;
			String refreshToken = null;

			try {
				authToken = parsedResponse.getString("access_token");
				refreshToken = parsedResponse.getString("refresh_token");
			} catch (final JSONException ignored) {
			}

			NetworkClientHolder.get().getToken().setAuthToken(authToken);
			NetworkClientHolder.get().getToken().setRefreshToken(refreshToken);

			final Account account = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE)[0];
			accountManager.setUserData(account, AccountUtils.KEY_REFRESH_TOKEN, refreshToken);
			accountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
			return true;
		}
	}

	private Request buildRefreshTokenRequest()
	{
		final JSONObject body = new JSONObject();

		try {
			body.put("clientId", ProprConfiguration.CLIENT_ID);
			body.put("clientSecret", ProprConfiguration.CLIENT_SECRET);
		} catch (final JSONException ignored) {}

		final String authorizationHeader = "Basic " + Base64.encodeToString(("app:secret").getBytes(), Base64.NO_WRAP);

		return new Request.Builder()
				.url(NetworkClientHolder.get().getConfiguration().getBackendUrl() + "/oauth/token?grant_type=refresh_token")
				.header("Authorization", "Bearer " + authorizationHeader)
				.post(RequestBody.create(FORM_ENCODED, body.toString()))
				.build();
	}

	private JSONObject getRefreshTokenResponse(final Request request) {
		final OkHttpClient client = NetworkClientHolder.get().getOkHttpClient();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (final IOException e) {
			Log.e(TAG, "Failed to refresh token: " + e.getMessage());
			e.printStackTrace();
		}

		if (response == null) {
			return null;
		}

		JSONObject parsedResponse = null;
		String responseString = null;
		try {
			responseString = response.body().string();
			parsedResponse = new JSONObject(responseString);
		} catch (JSONException | IOException ignored) {
			Log.e(TAG, "Unable to parse response: " + responseString);
			ignored.printStackTrace();
		}

		return parsedResponse;
	}
}
