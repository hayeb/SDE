package giphouse.nl.proprapp.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprConfiguration;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author haye
 */
public class OAuthRequestInterceptor implements Interceptor {

	private static final String TAG = "OAuthRequestInterceptor";

	private static final MediaType FORM_ENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

	private final AccountManager accountManager;

	private OkHttpClient okHttpClient;

	private final ProprConfiguration proprConfiguration;

	@Inject
	public OAuthRequestInterceptor(final AccountManager accountManager, final ProprConfiguration proprConfiguration) {
		this.accountManager = accountManager;
		this.proprConfiguration = proprConfiguration;
	}

	public void initHttpClient(final OkHttpClient client) {
		this.okHttpClient = client;
	}

	@Override
	public Response intercept(@NonNull final Chain chain) throws IOException {
		Request request = chain.request();

		if (!isInterceptable(request)) {
			return chain.proceed(request);
		}

		final Request.Builder builder = request.newBuilder();

		final Account account = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE)[0];
		final String authToken;

		try {
			authToken = accountManager.blockingGetAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, false);
		} catch (OperationCanceledException | AuthenticatorException e) {
			Log.e(TAG, "Unable to get token from account manager");
			return chain.proceed(request);
		}
		setAuthHeader(builder, authToken);

		request = builder.build();
		final Response response = chain.proceed(request);

		if (response.code() != 401) {
			return response;
		}

		final String newAuthToken = refreshToken(accountManager.getUserData(account, AccountUtils.KEY_REFRESH_TOKEN));
		if (newAuthToken == null) {
			return response;
		}
		setAuthHeader(builder, newAuthToken);
		request = builder.build();
		return chain.proceed(request);
	}

	private void setAuthHeader(final Request.Builder builder, final String token) {
		if (token != null) {
			builder.header("Authorization", String.format("Bearer %s", token));
		}
	}

	@SuppressWarnings("SynchronizeOnNonFinalField")
	private String refreshToken(final String refreshToken) {
		if (refreshToken == null) {
			Log.e(TAG, "Not trying to refresh token: refreshtoken is null");
			return null;
		}
		final Request request = buildRefreshTokenRequest(refreshToken);

		synchronized (okHttpClient) {
			final JSONObject parsedResponse = getRefreshTokenResponse(request);

			final String validationMessage;
			if ((validationMessage = validateRefreshResponse(parsedResponse)) != null) {
				Log.e(TAG, validationMessage);
				return null;
			}

			final String newAuthToken;
			final String newRefreshToken;
			try {
				newAuthToken = parsedResponse.getString("access_token");
				newRefreshToken = parsedResponse.getString("refresh_token");
			} catch (final JSONException ignored) {
				return null;
			}

			final Account account = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE)[0];
			accountManager.setUserData(account, AccountUtils.KEY_REFRESH_TOKEN, newRefreshToken);
			accountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, newAuthToken);
			return newAuthToken;
		}
	}

	private boolean isInterceptable(final Request request) {
		// Ensures that requests to token endpoints are ignored by this interceptor
		final String url = request.url().toString();
		if (url.contains("/api/users/register")) {
			Log.d(TAG, "Requests to user register endpoint not intercepted");
			return false;
		}
		if (url.contains("/api/")) {
			Log.d(TAG, "Request to an /api/ url intercepted");
			return true;
		}
		return false;
	}

	private String validateRefreshResponse(final JSONObject parsedResponse) {
		if (parsedResponse == null) {
			return "Unable to get response for refresh token request";
		}

		if (!parsedResponse.has("access_token") || !parsedResponse.has("refresh_token")) {
			return "Response does not have required fields \"acces_token\" or \"refresh_token\"";
		}
		return null;
	}

	private Request buildRefreshTokenRequest(final String refreshToken) {
		final JSONObject body = new JSONObject();

		try {
			body.put("refresh_token", refreshToken);
		} catch (final JSONException ignored) {
		}

		final String authorizationHeader = "Basic " + Base64.encodeToString((proprConfiguration.getClientId() + ":" + proprConfiguration.getClientSecret()).getBytes(), Base64.NO_WRAP);

		return new Request.Builder()
			.url(proprConfiguration.getBackendUrl() + "/oauth/token?grant_type=refresh_token")
			.header("Authorization", authorizationHeader)
			.post(RequestBody.create(FORM_ENCODED, body.toString()))
			.build();
	}

	private JSONObject getRefreshTokenResponse(final Request request) {
		final Response response;
		try {
			response = okHttpClient.newCall(request).execute();
		} catch (final IOException e) {
			Log.e(TAG, "Failed to refresh token: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return getParsedResponse(response.body());
	}

	private JSONObject getParsedResponse(final ResponseBody body) {
		if (body == null) {
			Log.e(TAG, "Could not get body from response, body was null");
			return null;
		}

		JSONObject parsedResponse = null;
		String responseString = null;
		try {
			responseString = body.string();
			parsedResponse = new JSONObject(responseString);
		} catch (JSONException | IOException ignored) {
			Log.e(TAG, "Unable to parse response: " + responseString);
			ignored.printStackTrace();
		}

		return parsedResponse;
	}
}
