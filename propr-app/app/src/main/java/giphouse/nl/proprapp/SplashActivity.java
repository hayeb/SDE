package giphouse.nl.proprapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.ui.LoginActivity;
import giphouse.nl.proprapp.ui.group.GroupListActivity;

public class SplashActivity extends AppCompatActivity {

	private static final String TAG = "SplashActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		final AccountManager accountManager = AccountManager.get(this);
		final Account[] accounts = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE);

		if (accounts.length == 0) {
			Log.i(TAG, "No account found on device. Starting LoginActivity");
			startActivityForResult(new Intent(this, LoginActivity.class), 11);
		} else {

			final Account account = accounts[0];

			accountManager.getAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, null, false, result -> {
				Bundle bundle = null;
				try {
					bundle = result.getResult();
				} catch (OperationCanceledException | IOException | AuthenticatorException ignored) {
				}

				if (bundle == null) {
					Log.e(TAG, "Unable to get auth token from account manager");
					return;
				}
				final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
				final String refreshToken = accountManager.getUserData(account, AccountUtils.KEY_REFRESH_TOKEN);
				if (authToken == null || refreshToken == null) {
					Log.i(TAG, "No auth, refresh token found. Starting Login Activity");
					startActivityForResult(new Intent(this, LoginActivity.class), 11);
					return;
				}
				startActivity(new Intent(this, GroupListActivity.class));
			}, null);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == 11) {
			startActivity(new Intent(this, GroupListActivity.class));
		}
	}
}
