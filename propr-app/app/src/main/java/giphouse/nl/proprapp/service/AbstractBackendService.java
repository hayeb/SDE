package giphouse.nl.proprapp.service;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import java.io.IOException;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author haye
 */
public class AbstractBackendService {

	protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	protected static final OkHttpClient client = new OkHttpClient.Builder().build();

	public Request.Builder buildBackendCall(final String url, final Context context) {
		final AccountManager accountManager = AccountManager.get(context);
		final String authToken;
		try {
			// TODO: Dit vraagt niet opnieuw naar credentials als de token niet meer geldig is..
			authToken = accountManager.blockingGetAuthToken(accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE)[0], AccountUtils.AUTH_TOKEN_TYPE, false);
		} catch (OperationCanceledException | IOException | AuthenticatorException e) {
			e.printStackTrace();
			return null;
		}

		if (authToken == null) {
			return null;
		}

		return new Request.Builder()
			.url(context.getString(R.string.backend_url) + url)
			.header("Authorization", "Bearer " + authToken);
	}
}
