package giphouse.nl.proprapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.AuthenticatorService;
import giphouse.nl.proprapp.account.ui.LoginActivity;
import giphouse.nl.proprapp.ui.group.GroupListActivity;

public class SplashActivity extends AppCompatActivity {

	private static final String TAG = "SplashActivity";

	@Inject
	AuthenticatorService authenticatorService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		((ProprApplication) getApplication()).getComponent().inject(this);

		final AccountManager accountManager = AccountManager.get(this);
		final Account[] accounts = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE);

		if (accounts.length == 0) {
			Log.d(TAG, "No account found on device. Starting LoginActivity");
			startActivityForResult(new Intent(this, LoginActivity.class), 11);
		} else {
			Log.d(TAG, "Account found on device. Getting additional information");
			final Account account = accounts[0];

			accountManager.getAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, null, false, new AccountManagerCallback<Bundle>() {
				@Override
				public void run(final AccountManagerFuture<Bundle> result) {
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
						SplashActivity.this.startActivityForResult(new Intent(SplashActivity.this, LoginActivity.class), 11);
						return;
					}

					new TokenValidTask(authenticatorService, new WeakReference<>(SplashActivity.this), account.name).execute(authToken);

				}
			}, null);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == 11) {
			startActivity(new Intent(this, GroupListActivity.class));
		}
	}

	private static class TokenValidTask extends AsyncTask<String, Void, Boolean> {
		private final AuthenticatorService authenticatorService;

		private final WeakReference<SplashActivity> contextReference;

		private final String username;

		TokenValidTask(final AuthenticatorService authenticatorService, final WeakReference<SplashActivity> contextReference, final String username) {
			this.authenticatorService = authenticatorService;
			this.contextReference = contextReference;
			this.username = username;
		}

		@Override
		protected Boolean doInBackground(final String... tokens) {
			if (tokens == null || tokens.length != 1) {
				throw new IllegalArgumentException("TokenValidTask accepts exactly 1 token as parameter");
			}

			return authenticatorService.tokenValid(tokens[0]);
		}

		@Override
		protected void onPostExecute(final Boolean tokenValid) {
			final Context context = contextReference.get();
			if (context == null)
			{
				Log.e(TAG, "No context attached to TokenValidTask");
				return;
			}
			if (tokenValid) {
				context.startActivity(new Intent(context, GroupListActivity.class));
				return;
			}
			final Intent intent = new Intent(context, LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_REASON_KEY, context.getString(R.string.expired_login_reason));
			intent.putExtra(LoginActivity.USERNAME_KEY, username);
			contextReference.get().startActivityForResult(intent, LoginActivity.CODE_LOGGED_IN);

		}
	}

	@Override
	public void onActivityReenter(final int resultCode, final Intent data) {
		if (resultCode ==  LoginActivity.CODE_LOGGED_IN) {
			startActivity(new Intent(this, GroupListActivity.class));
		}
	}
}
