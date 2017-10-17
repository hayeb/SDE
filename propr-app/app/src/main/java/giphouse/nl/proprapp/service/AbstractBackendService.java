package giphouse.nl.proprapp.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

import giphouse.nl.proprapp.account.AccountUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * @author haye
 */
public class AbstractBackendService {

	protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static final OkHttpClient client = new OkHttpClient.Builder().build();

	private final AccountManager accountManager;

	private final String backendUrl;

	public AbstractBackendService(final AccountManager accountManager, final String backendUrl) {
		this.accountManager = accountManager;
		this.backendUrl = backendUrl;
	}

	protected Request.Builder buildBackendCall(final String url) {
		final Account[] proprAccounts = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE);

		if (ArrayUtils.isEmpty(proprAccounts))
		{
			throw new IllegalStateException("No account present when making backend call!");
		}

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
			.url(backendUrl + url)
			.header("Authorization", "Bearer " + authToken);
	}

	protected String doCall(final Request.Builder builder)
	{
		try {
			final ResponseBody body = client.newCall(builder.build()).execute().body();
			if (body == null)
			{
				return null;
			}
			return body.string();
		} catch (final IOException e) {
			return null;
		}
	}
}
