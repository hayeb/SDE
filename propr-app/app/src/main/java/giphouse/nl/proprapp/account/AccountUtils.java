package giphouse.nl.proprapp.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;

/**
 * @author haye
 */
public class AccountUtils {

	private static final String TAG = "AccountUtils";

	static final String KEY_ACCOUNT_TYPE = "accountType";

	static final String KEY_AUTH_TYPE = "authType";

	static final String KEY_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";

	public static final String ACCOUNT_TYPE = "nl.giphouse.propr";

	public static final String AUTH_TOKEN_TYPE = "nl.giphouse.propr.auth_token";

	public static final String KEY_REFRESH_TOKEN = "nl.giphouse.propr.token";

	public static String getUsername(@Nonnull final Context context) {
		final AccountManager accountManager = AccountManager.get(context);
		final Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);

		if (ArrayUtils.isEmpty(accounts)) {
			Log.e(TAG, "No Propr user accounts found!");
			return null;
		}

		if (accounts.length > 1) {
			Log.e(TAG, "More than one Propr account on this device!");
			return null;
		}

		return accounts[0].name;
	}
}
