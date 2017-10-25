package giphouse.nl.proprapp.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import giphouse.nl.proprapp.account.ui.LoginActivity;


/**
 * @author haye
 */
public class ProprAuthenticator extends AbstractAccountAuthenticator {

	private static final String TAG = "ProprAuthenticator";

	private final Context mContext;

	private final AuthenticatorService authenticatorService;

	@Inject
	public ProprAuthenticator(final Context context, final AuthenticatorService authenticatorService) {
		super(context);

		mContext = context;
		this.authenticatorService = authenticatorService;
	}

	@Override
	public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
		return null;
	}

	@Override
	public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType, final String authTokenType, final String[] requiredFeatures, final Bundle options) throws NetworkErrorException {
		final Intent intent = new Intent(mContext, LoginActivity.class);

		intent.putExtra(AccountUtils.KEY_ACCOUNT_TYPE, accountType);
		intent.putExtra(AccountUtils.KEY_AUTH_TYPE, authTokenType);
		intent.putExtra(AccountUtils.KEY_IS_ADDING_NEW_ACCOUNT, true);

		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);

		return bundle;
	}

	@Override
	public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account, final Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle options) throws NetworkErrorException {
		final AccountManager am = AccountManager.get(mContext);
		String authToken = am.peekAuthToken(account, authTokenType);
		String refreshToken = am.getUserData(account, AccountUtils.KEY_REFRESH_TOKEN);


		if (TextUtils.isEmpty(authToken)) {
			final String password = am.getPassword(account);
			if (password != null) {
				final Token token = authenticatorService.signIn(account.name, password);
				authToken = token.getAuthToken();
				refreshToken = token.getRefreshToken();
			}
		}

		if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(refreshToken)) {
			Log.d(TAG, "Found auth and refresh token.");
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			result.putString(AccountUtils.KEY_REFRESH_TOKEN, refreshToken);
			return result;
		}
		Log.d(TAG, "Not auth, refresh token found. Starting login activity.");
		final Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		intent.putExtra(AccountUtils.KEY_ACCOUNT_TYPE, account.type);
		intent.putExtra(AccountUtils.KEY_AUTH_TYPE, authTokenType);
		intent.putExtra("full_access", authTokenType);

		final Bundle retBundle = new Bundle();
		retBundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return retBundle;
	}

	@Override
	public String getAuthTokenLabel(final String authTokenType) {
		return "Propr token";
	}

	@Override
	public Bundle updateCredentials(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account, final String[] features) throws NetworkErrorException {
		return null;
	}
}
