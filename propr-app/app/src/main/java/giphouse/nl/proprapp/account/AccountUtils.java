package giphouse.nl.proprapp.account;

/**
 * @author haye
 */
public class AccountUtils {
	public static final String ACCOUNT_TYPE = "nl.giphouse.propr";

	public static final String AUTH_TOKEN_TYPE = "nl.giphouse.propr.auth_token";

	public static final String KEY_ACCOUNT_TYPE = "accountType";

	public static final String KEY_AUTH_TYPE = "authType";

	public static final String KEY_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";

	public static final String KEY_REFRESH_TOKEN = "nl.giphouse.propr.token";

	public static BackendAuthenticator mServerAuthenticator = new BackendAuthenticator();
}
