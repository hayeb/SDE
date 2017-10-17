package giphouse.nl.proprapp;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.ui.groups.MainActivity;

/**
 * Splash activity to ensure that the user is logged in, or prompts to make an account
 */
public class SplashActivity extends AppCompatActivity {

	private static final String TAG = "SplashActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Log.i(TAG, "Checked credentials. Starting main");
		AccountManager.get(this).getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, result -> {
			final Bundle bundle;
			try {
				bundle = result.getResult();

				// An intent is returned: Start it to acquire proper account credentials
				final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					this.startActivityForResult(intent, 1);
				}

				final String authToken = (String) bundle.get(AccountManager.KEY_AUTHTOKEN);
				if (authToken == null) {
					Log.i(TAG, "Account present, but no auth token: Ask for credentials");
				}
				Log.i(TAG, "Valid credentials found");
				startActivity(new Intent(this, MainActivity.class));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}, null);
	}
}
