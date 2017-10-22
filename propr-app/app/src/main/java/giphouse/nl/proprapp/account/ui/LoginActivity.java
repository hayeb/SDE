package giphouse.nl.proprapp.account.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.BackendAuthenticator;
import giphouse.nl.proprapp.account.Token;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

	private static final String TAG = "LoginActivity";

	@Inject
	BackendAuthenticator backendAuthenticator;

	@Inject
	SharedPreferences sharedPreferences;

	// UI references.
	private AutoCompleteTextView mUsernameTextView;
	private EditText mPasswordView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_login);
		// Set up the login form.
		mUsernameTextView = findViewById(R.id.email);

		mPasswordView = findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
			if (id == R.id.login || id == EditorInfo.IME_NULL) {
				attemptLogin();
				return true;
			}
			return false;
		});

		final Button mSignInButton = findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(view -> attemptLogin());

		final Button mRegisterAccountButton = findViewById(R.id.register_account_button);
		mRegisterAccountButton.setOnClickListener(view -> createAccount());
	}

	@SuppressLint("StaticFieldLeak")
	private void attemptLogin() {
		mUsernameTextView.setError(null);
		mPasswordView.setError(null);

		final String username = mUsernameTextView.getText().toString();
		final String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(username)) {
			mUsernameTextView.setError(getString(R.string.error_field_required));
			focusView = mUsernameTextView;
			cancel = true;
		} else if (!isUsernameValid(username)) {
			mUsernameTextView.setError(getString(R.string.error_invalid_username));
			focusView = mUsernameTextView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			new AsyncTask<Void, Void, Intent>() {

				@Override
				protected Intent doInBackground(final Void... voids) {
					Log.i(TAG, "Logging in as: " + username);

					final Token authToken = backendAuthenticator.signIn(username, password);

					if (authToken == null) {
						Log.d(TAG, "Signing in unsuccessful. Token is null");
						return null;
					}

					if (authToken.getAuthToken() == null) {
						Log.d(TAG, "Signing in seems successful, but auth token is null");
						return null;
					}

					if (authToken.getRefreshToken() == null) {
						Log.d(TAG, "Signing in seems successful, but refresh token is null");
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
					finishLogin(intent, username, password);
				}
			}.execute();
		}
	}

	private void finishLogin(final Intent intent, final String username, final String password) {
		if (intent == null || intent.getExtras() == null) {
			return;
		}

		final AccountManager accountManager = AccountManager.get(LoginActivity.this);
		final Account acc = new Account(username, AccountUtils.ACCOUNT_TYPE);
		accountManager.addAccountExplicitly(acc, password, null);

		accountManager.setUserData(acc, AccountUtils.KEY_REFRESH_TOKEN, intent.getExtras().getString(AccountUtils.KEY_REFRESH_TOKEN));

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	private void createAccount() {
		setContentView(R.layout.activity_create_account);

		final Intent intent = new Intent(this, RegisterAccountActivity.class);
		startActivity(intent);
	}

	private boolean isUsernameValid(final String username) {
		return username.length() > 5;
	}
}

