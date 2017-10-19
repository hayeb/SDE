package giphouse.nl.proprapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.OAuthRequestInterceptor;
import giphouse.nl.proprapp.account.Token;
import giphouse.nl.proprapp.ui.groups.MainActivity;
import okhttp3.OkHttpClient;

/**
 * Splash activity to ensure that the user is logged in, or prompts to make an account
 */
public class SplashActivity extends AppCompatActivity {

	private static final String TAG = "SplashActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		AccountManager.get(this).getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, result -> {
			final Bundle bundle;
			try {
				bundle = result.getResult();

				// An intent is returned: Start it to acquire proper account credentials
				final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					this.startActivityForResult(intent, 1);
				}

				final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
				if (authToken == null) {
					Log.i(TAG, "Account present, but no auth token: Ask for credentials");
					// TODO: Actually ask..
				}
				Log.i(TAG, "Valid credentials found for ");

				initializeApplication(authToken, bundle.getString(AccountUtils.KEY_REFRESH_TOKEN));
				startActivity(new Intent(this, MainActivity.class));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}, null);
	}

	/**
	 * Configureer de NetworkClientHolder, zodat we die later kunnen gebruiken om authenticated requests te maken naar de juiste backend.
	 */
	private void initializeApplication(final String authToken, String refreshToken) {
		final ProprConfiguration config = new ProprConfiguration(getString(R.string.backend_url));
		final AccountManager accountManager = AccountManager.get(this);
		final Account account = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE)[0];

		if (refreshToken == null) {
			refreshToken = accountManager.getUserData(account, AccountUtils.KEY_REFRESH_TOKEN);
		}

		final Token token = new Token(authToken, refreshToken);
		final OAuthRequestInterceptor interceptor = new OAuthRequestInterceptor(accountManager);
		final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

		NetworkClientHolder.init(client, config, token);
	}
}
