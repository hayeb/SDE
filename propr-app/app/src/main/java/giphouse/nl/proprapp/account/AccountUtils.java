package giphouse.nl.proprapp.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * @author haye
 */
public class AccountUtils {
	public static final String ACCOUNT_TYPE = "nl.giphouse.propr";

	public static final String AUTH_TOKEN_TYPE = "nl.giphouse.propr.auth_token";

	public static final String ARG_ACCOUNT_TYPE = "accountType";

	public static final String ARG_AUTH_TYPE = "authType";

	public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";

	public static BackendAuthenticator mServerAuthenticator = new BackendAuthenticator();

	public static Account getAccount(final Context context, final String accountName) {
		final AccountManager accountManager = AccountManager.get(context);
		final Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
		for (final Account account : accounts) {
			if (account.name.equalsIgnoreCase(accountName)) {
				return account;
			}
		}
		return null;
	}
}
