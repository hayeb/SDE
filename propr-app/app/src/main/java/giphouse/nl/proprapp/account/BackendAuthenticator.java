package giphouse.nl.proprapp.account;

import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

	private static final OkHttpClient client = new OkHttpClient.Builder().build();

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static final String CLIENT_NAME = "app";

	private static final String CLIENT_SECRET = "secret";

	public String signUp(final String email, final String username, final String password, final String backendUrl) {
		// 1. Get initial token from backend
		final String clientToken = getInitialClientToken(backendUrl);
		if (StringUtils.isEmpty(clientToken)) {
			Log.d(TAG, "No token received from backend");
			return null;
		}

		// 2. Create user using initial token, and authenticate.
		final boolean userCreated = createUser(backendUrl, clientToken, username, password, email);
		if (!userCreated) {
			return null;
		}

		// 3. Create user account in account manager
		return signIn(backendUrl, username, password);
	}

	private String getInitialClientToken(final String backendUrl) {
		try {
			final Request request = new Request.Builder()
					.url(backendUrl + "/oauth/token?grant_type=client_credentials")
					.post(RequestBody.create(JSON, ""))
					.header("Authorization", "Basic " + Base64.encodeToString((CLIENT_NAME + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP))
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

	private boolean createUser(final String backendUrl, final String clientToken, final String username, final String password, final String email) {
		try {
			final JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", username);
			jsonObject.put("email", email);
			jsonObject.put("password", password);
			final Request request = new Request.Builder()
					.header("Authorization", "Bearer " + clientToken)
					.url(backendUrl + "/api/users/register")
					.post(RequestBody.create(JSON, jsonObject.toString()))
					.build();
			final Response response = client.newCall(request).execute();

			if (!response.isSuccessful()) {
				Log.e(TAG, "Unable to register user with backend. Response: [" + response.code() + "] " + getBodyString(response));
				return false;

			}
			Log.d(TAG, "Created user " + username);
			return true;
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String signIn(final String backendUrl, final String username, final String password) {
		try {
			final Request request = new Request.Builder()
					.url(backendUrl + "/oauth/token?grant_type=password&username=" + username + "&password=" + password)
					.header("Authorization", "Basic " + Base64.encodeToString((CLIENT_NAME + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP))
					.post(RequestBody.create(JSON, ""))
					.build();

			final Response response = client.newCall(request).execute();
			final String responseBody = getBodyString(response);

			if (!response.isSuccessful()) {
				Log.e(TAG, String.format("Unable to log in using [%s:%s] using token %s. Response: %s", username, password, CLIENT_NAME, responseBody));
				return null;
			}

			Log.d(TAG, "Signed in as " + username);
			return new JSONObject(responseBody).getString("access_token");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getBodyString(final Response response)
	{
		final ResponseBody body = response.body();
		if (body == null) { return null;}
		try {
			return body.string();
		} catch (final IOException e) {
			Log.e(TAG, "Unable to get responsebody");
			return null;
		}
	}
}
