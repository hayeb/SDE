package giphouse.nl.proprapp.account.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ActionBar;
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
import android.widget.Toolbar;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.AuthenticatorService;
import giphouse.nl.proprapp.account.Token;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

	public static String LOGIN_REASON_KEY = "loginReason";

	public static String USERNAME_KEY = "username";

	public static int CODE_LOGGED_IN = 11;

	private static final String TAG = "LoginActivity";

	@Inject
	AuthenticatorService authenticatorService;

	@Inject
	SharedPreferences sharedPreferences;

	// UI references.
	private EditText mUsernameTextView;
	private EditText mPasswordView;

	private AccountManager accountManager;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		accountManager = AccountManager.get(this);

		setContentView(R.layout.activity_login);
		setActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getActionBar();
		if (bar != null) {
			bar.setDisplayShowTitleEnabled(true);
		}

		final Bundle extras = getIntent().getExtras();

		// Set up the login form.
		final TextView mLoginReasonTextView = findViewById(R.id.login_reason_text);
		mUsernameTextView = findViewById(R.id.email);
		mPasswordView = findViewById(R.id.password);

		// Show the reason for logging in
		if (extras != null && extras.containsKey(LOGIN_REASON_KEY)) {
			mLoginReasonTextView.setText(extras.getString(LOGIN_REASON_KEY));
		}
		// Fill the username field if one is provided
		if (extras != null && extras.containsKey(USERNAME_KEY)) {
			mLoginReasonTextView.setText(extras.getString(USERNAME_KEY));
		}

		final Button mSignInButton = findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				LoginActivity.this.attemptLogin();
			}
		});

		final Button mRegisterAccountButton = findViewById(R.id.register_account_button);
		mRegisterAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				LoginActivity.this.startCreateAccountActivity();
			}
		});
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
			Toast.makeText(this, "Unable to log in: Incorrect username or password", Toast.LENGTH_LONG).show();
			return;
		}

		final AccountManager accountManager = AccountManager.get(LoginActivity.this);
		final Account[] accounts = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE);

		if (accounts.length > 1) {
			throw new IllegalStateException("Multiple accounts registered on device!");
		}

		if (differentAccountRegistered(username, accounts)) {
			Log.d(TAG, "Renaming earlier account");
			renameAccount(accounts[0], username, password, intent);
			return;
		}

		final Account account = getNewOrExistingAccount(accounts, username, password);

		Log.d(TAG, "Updating password and refresh token for account");
		accountManager.setPassword(account, password);
		accountManager.setUserData(account, AccountUtils.KEY_REFRESH_TOKEN, intent.getExtras().getString(AccountUtils.KEY_REFRESH_TOKEN));

		returnResult(intent);
	}

	private void renameAccount(final Account account, final String username, final String password, final Intent intent) {
		final Bundle extras = intent.getExtras();
		accountManager.renameAccount(account, username, new AccountManagerCallback<Account>() {
				@Override
				public void run(final AccountManagerFuture<Account> acc) {
					final Account renamedAccount;
					try {
						renamedAccount = acc.getResult();
					} catch (OperationCanceledException | IOException | AuthenticatorException e) {
						e.printStackTrace();
						LoginActivity.this.setResult(1);
						LoginActivity.this.finish();
						return;
					}

					LoginActivity.this.setAdditionalData(renamedAccount, password, extras.getString(AccountUtils.KEY_REFRESH_TOKEN));
					LoginActivity.this.returnResult(intent);
				}
			}
			, null);
	}

	private void setAdditionalData(final Account account, final String password, final String refreshToken) {
		accountManager.setPassword(account, password);
		accountManager.setUserData(account, AccountUtils.KEY_REFRESH_TOKEN, refreshToken);
	}

	private void returnResult(final Intent intent) {
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(CODE_LOGGED_IN, intent);
		finish();
	}

	private boolean differentAccountRegistered(final String username, final Account[] accounts) {
		return accounts.length == 1 && !accounts[0].name.equals(username);
	}

	private Account getNewOrExistingAccount(final Account[] accounts, final String username, final String password) {
		if (accounts.length == 0) {
			final Account account = new Account(username, AccountUtils.ACCOUNT_TYPE);
			accountManager.addAccountExplicitly(account, password, null);
			return account;
		} else {
			return accounts[0];
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode == RegisterAccountActivity.CODE_ACCOUNT_REGISTERED) {
			setAccountAuthenticatorResult(data.getExtras());
			setResult(CODE_LOGGED_IN, data);
			finish();
		}
	}

	private void startCreateAccountActivity() {
		final Intent intent = new Intent(this, RegisterAccountActivity.class);
		startActivityForResult(intent, RegisterAccountActivity.CODE_ACCOUNT_REGISTERED);
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

	private static class LoginTask extends AsyncTask<Void, Void, Intent> {

		private final WeakReference<LoginActivity> loginActivityWeakReference;
		private final AuthenticatorService authenticatorService;
		private final SharedPreferences sharedPreferences;

		private final String username;
		private final String password;

		LoginTask(final WeakReference<LoginActivity> loginActivityWeakReference, final AuthenticatorService authenticatorService, final SharedPreferences sharedPreferences, final String username, final String password) {
			this.loginActivityWeakReference = loginActivityWeakReference;
			this.authenticatorService = authenticatorService;
			this.sharedPreferences = sharedPreferences;
			this.username = username;
			this.password = password;
		}

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
			final LoginActivity loginActivity = loginActivityWeakReference.get();
			if (loginActivity != null) {
				loginActivity.finishLogin(intent, username, password);
			}
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

