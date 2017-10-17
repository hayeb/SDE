package giphouse.nl.proprapp.ui.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.BackendAuthenticator;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

	private UserLoginTask userLoginTask = null;

	private AccountManager mAccountManager;
	private String authToken;

	// UI references.
	private AutoCompleteTextView mUsernameTextView;
	private EditText mPasswordView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userLoginTask = (UserLoginTask) getLastNonConfigurationInstance();

		if (userLoginTask != null) {
			userLoginTask.mActivity = this;
		}

		authToken = null;
		mAccountManager = AccountManager.get(this);

		mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, result -> {
			final Bundle bundle;
			try {
				bundle = result.getResult();

				final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					startActivityForResult(intent, 1);
				} else {
					authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

					// If the logged account didn't exist, we need to create it on the device
					Account account = AccountUtils.getAccount(LoginActivity.this, accountName);
					if (null == account) {
						account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
						mAccountManager.addAccountExplicitly(account, bundle.getString("password"), null);
						mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
					}

				}
			} catch (final OperationCanceledException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				Log.e("register.error", "Exception!");
				e.printStackTrace();
			}
		}, null);

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

	@Override
	public Object onRetainNonConfigurationInstance() {
		return userLoginTask;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (userLoginTask != null) {
			userLoginTask.mActivity = null;
		}
	}

	private void attemptLogin() {
		if (userLoginTask != null) {
			return;
		}

		mUsernameTextView.setError(null);
		mPasswordView.setError(null);

		final String username = mUsernameTextView.getText().toString();
		final String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
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
			userLoginTask = new UserLoginTask(username, password, this);
			userLoginTask.execute();
		}
	}

	private void createAccount() {
		setContentView(R.layout.activity_create_account);

		final Intent intent = new Intent(this, RegisterAccountActivity.class);
		startActivity(intent);
	}

	private boolean isUsernameValid(final String username) {
		return username.length() > 5;
	}

	private boolean isPasswordValid(final String password) {
		return password.length() > 7;
	}

	static class UserLoginTask extends AsyncTask<Void, Void, Boolean>
	{
		private final String mUsername;

		private final String mPassword;

		@SuppressLint("StaticFieldLeak")
		private LoginActivity mActivity;

		UserLoginTask(final String username, final String password, final LoginActivity activity) {
			mUsername = username;
			mPassword = password;
			mActivity = activity;
		}

		@Override
		protected Boolean doInBackground(final Void... accountManagers) {
			if (accountManagers == null || accountManagers.length != 1)
			{
				throw new IllegalArgumentException("A single account manager should be passed in!");
			}

			Log.e("Propr", "Logging in as: " + mUsername + " with password " + mPassword);

			final String authToken = new BackendAuthenticator().signIn(mActivity.getString(R.string.backend_url), mUsername, mPassword);

			if (StringUtils.isEmpty(authToken)) {
				return false;
			}

			final AccountManager accountManager = AccountManager.get(mActivity);
			final Account acc = new Account(mUsername, AccountUtils.ACCOUNT_TYPE);
			accountManager.addAccountExplicitly(acc, mPassword, null);
			accountManager.setAuthToken(acc, AccountUtils.AUTH_TOKEN_TYPE, authToken);
			return true;
		}
	}

}

