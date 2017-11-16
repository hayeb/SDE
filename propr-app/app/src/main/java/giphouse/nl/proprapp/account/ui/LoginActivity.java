package giphouse.nl.proprapp.account.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.AuthenticatorService;
import giphouse.nl.proprapp.account.Token;
import lombok.AllArgsConstructor;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

	public static String LOGIN_REASON_KEY = "loginReason";

	public static String USERNAME_KEY = "username";

	private static final String TAG = "LoginActivity";

	@Inject
	AuthenticatorService authenticatorService;

	@Inject
	SharedPreferences sharedPreferences;

	// UI references.
	private EditText mUsernameTextView;
	private EditText mPasswordView;
	private TextView mLoginReasonTextView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_login);
		setActionBar(findViewById(R.id.toolbar));
		getActionBar().setDisplayShowTitleEnabled(true);

		final Bundle extras = getIntent().getExtras();

		// Set up the login form.
		mLoginReasonTextView = findViewById(R.id.login_reason_text);
		mUsernameTextView = findViewById(R.id.email);
		mPasswordView = findViewById(R.id.password);

		// Show the reason for logging in
		Optional.ofNullable(extras)
			.map(e -> e.getString(LOGIN_REASON_KEY))
			.ifPresent(mLoginReasonTextView::setText);

		// Fill the username field if one is provided
		Optional.ofNullable(extras)
			.map(e -> e.getString(USERNAME_KEY))
			.ifPresent(mUsernameTextView::setText);

		final Button mSignInButton = findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(view -> attemptLogin());

		final Button mRegisterAccountButton = findViewById(R.id.register_account_button);
		mRegisterAccountButton.setOnClickListener(view -> startCreateAccountActivity());
	}

	private void attemptLogin() {
		mUsernameTextView.setError(null);
		mPasswordView.setError(null);

		final String username = mUsernameTextView.getText().toString();
		final String password = mPasswordView.getText().toString();

		final View focusView = validateInput(username, password);
		if (focusView != null) {
			focusView.requestFocus();
			return;
		}
		new LoginTask(new WeakReference<>(this), authenticatorService, sharedPreferences, username, password)
			.execute();
	}

	private void finishLogin(final Intent intent, final String username, final String password) {
		if (intent == null || intent.getExtras() == null) {
			Toast.makeText(this, "Unable to log in: Incorrect username or password", Toast.LENGTH_SHORT).show();
			return;
		}

		final AccountManager accountManager = AccountManager.get(LoginActivity.this);

		//Arrays.stream(accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE))
		//	.forEach(account -> accountManager.removeAccount(account, this, null,null));

		final Account acc = new Account(username, AccountUtils.ACCOUNT_TYPE);
		accountManager.addAccountExplicitly(acc, password, null);
		accountManager.setUserData(acc, AccountUtils.KEY_REFRESH_TOKEN, intent.getExtras().getString(AccountUtils.KEY_REFRESH_TOKEN));

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(11, intent);
		finish();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode == 11) {
			setAccountAuthenticatorResult(data.getExtras());
			setResult(11, data);
			finish();
		}
	}

	private void startCreateAccountActivity() {
		final Intent intent = new Intent(this, RegisterAccountActivity.class);
		startActivityForResult(intent, 11);
	}

	private boolean isUsernameValid(final String username) {
		return username.length() > 5;
	}

	private View validateInput(final String username, final String password) {
		View focusView = null;
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
		}
		if (TextUtils.isEmpty(username)) {
			mUsernameTextView.setError(getString(R.string.error_field_required));
			focusView = mUsernameTextView;
		} else if (!isUsernameValid(username)) {
			mUsernameTextView.setError(getString(R.string.error_invalid_username));
			focusView = mUsernameTextView;
		}
		return focusView;
	}

	@AllArgsConstructor
	private static class LoginTask extends AsyncTask<Void, Void, Intent> {

		private WeakReference<LoginActivity> loginActivityWeakReference;
		private AuthenticatorService authenticatorService;
		private SharedPreferences sharedPreferences;

		private String username;
		private String password;

		@Override
		protected Intent doInBackground(final Void... voids) {
			Log.d(TAG, "Logging in as: " + username);

			final Token authToken = authenticatorService.signIn(username, password);

			final String validationError = validateToken(authToken);
			if (validationError != null) {
				Log.e(TAG, validationError);
				return null;
			}

			Log.d(TAG, "Authentication succeeded");

			sharedPreferences.edit()
				.putString(AccountUtils.PREF_AUTH_TOKEN, authToken.getAuthToken())
				.putString(AccountUtils.PREF_REFRESH_TOKEN, authToken.getRefreshToken())
				.apply();

			final Intent intent = new Intent();
			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
			intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken.getAuthToken());
			intent.putExtra(AccountUtils.KEY_REFRESH_TOKEN, authToken.getRefreshToken());
			return intent;
		}

		@Override
		protected void onPostExecute(final Intent intent) {
			Optional.of(loginActivityWeakReference)
				.map(WeakReference::get)
				.ifPresent(a -> a.finishLogin(intent, username, password));
		}

		private String validateToken(final Token token) {
			if (token == null) {
				return "Signing in unsuccessful. Token is null";
			}

			if (token.getAuthToken() == null) {
				return "Signing in seems successful, but auth token is null";
			}

			if (token.getRefreshToken() == null) {
				return "Signing in seems successful, but refresh token is null";
			}
			return null;
		}
	}
}

