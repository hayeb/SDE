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

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.ui.account.RegisterAccountActivity;

/**
 * @author haye
 */
public class ProprAuthenticator extends AbstractAccountAuthenticator {

	private final Context mContext;

	private BackendAuthenticator backendAuthenticator;

	public ProprAuthenticator(final Context context) {
		super(context);

		mContext = context;
		backendAuthenticator = new BackendAuthenticator();
	}

	@Override
	public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
		return null;
	}

	@Override
	public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType, final String authTokenType, final String[] requiredFeatures, final Bundle options) throws NetworkErrorException {
		final Intent intent = new Intent(mContext, RegisterAccountActivity.class);

		// This key can be anything. Try to use your domain/package
		intent.putExtra(AccountUtils.ARG_ACCOUNT_TYPE, accountType);

		// This key can be anything too. It's just a way of identifying the token's type (used when there are multiple permissions)
		intent.putExtra(AccountUtils.ARG_AUTH_TYPE, authTokenType);

		// This key can be anything too. Used for your reference. Can skip it too.
		intent.putExtra(AccountUtils.ARG_IS_ADDING_NEW_ACCOUNT, true);

		// Copy this exactly from the line below.
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

		if (TextUtils.isEmpty(authToken)) {
			final String password = am.getPassword(account);
			if (password != null) {
				authToken = backendAuthenticator.signIn(mContext.getString(R.string.backend_url), account.name, password);
			}
		}

		if (!TextUtils.isEmpty(authToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}
		final Intent intent = new Intent(mContext, RegisterAccountActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		intent.putExtra(AccountUtils.ARG_ACCOUNT_TYPE, account.type);
		intent.putExtra(AccountUtils.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra("full_access", authTokenType);

		final Bundle retBundle = new Bundle();
		retBundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return retBundle;
	}

	@Override
	public String getAuthTokenLabel(final String authTokenType) {
		return null;
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
