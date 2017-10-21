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
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.BackendAuthenticator;
import giphouse.nl.proprapp.account.Token;
import giphouse.nl.proprapp.account.UserAccountDto;

/**
 * @author haye
 */
public class RegisterAccountActivity extends AccountAuthenticatorActivity {

	private static final String TAG = "RegisterAccountActivity";

	@Inject
	BackendAuthenticator backendAuthenticator;

	@Inject
	SharedPreferences sharedPreferences;

	private TextView mUsernameField;
	private TextView mEmailField;
	private TextView mPasswordField;
	private TextView mRepeatPasswordField;
	private TextView mFirstnameField;
	private TextView mLastnameField;

	private AccountManager mAccountManager;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);
		setContentView(R.layout.activity_create_account);

		mUsernameField = findViewById(R.id.username);
		mEmailField = findViewById(R.id.email);
		mPasswordField = findViewById(R.id.password);
		mRepeatPasswordField = findViewById(R.id.repeat_password);
		mFirstnameField = findViewById(R.id.firstname);
		mLastnameField = findViewById(R.id.lastname);

		mAccountManager = AccountManager.get(this);

		final Button registerAccountButton = findViewById(R.id.submit_account_button);
		registerAccountButton.setOnClickListener(view -> registerAccount());
	}

	@SuppressLint("StaticFieldLeak")
	private void registerAccount() {

		mUsernameField.setError(null);
		mEmailField.setError(null);
		mPasswordField.setError(null);
		mRepeatPasswordField.setError(null);
		mFirstnameField.setError(null);
		mLastnameField.setError(null);

		final String username = mUsernameField.getText().toString();
		final String email = mEmailField.getText().toString();
		final String password = mPasswordField.getText().toString();
		final String passwordRepeated = mRepeatPasswordField.getText().toString();
		final String firstname = mFirstnameField.getText().toString();
		final String lastname = mLastnameField.getText().toString();

		View view = null;

		if (!validateMatchingPasswords(password, passwordRepeated)) {
			mRepeatPasswordField.setError(getString(R.string.password_validation_not_matching));
			view = mRepeatPasswordField;
		}
		if (!validatePassword(password)) {
			mPasswordField.setError(getString(R.string.password_validation_size));
			view = mPasswordField;
		}
		if (!validateEmail(email)) {
			mEmailField.setError(getString(R.string.email_validation));
			view = mEmailField;
		}
		if (!validateLastname(lastname)) {
			mLastnameField.setError(getString(R.string.last_name_error));
			view = mLastnameField;
		}
		if (!validateFirstname(firstname)) {
			mFirstnameField.setError(getString(R.string.first_name_error));
			view = mFirstnameField;
		}
		if (!validateUsername(username)) {
			mUsernameField.setError(getString(R.string.username_validation_size));
			view = mUsernameField;
		}

		final UserAccountDto accountDto = UserAccountDto.builder()
			.username(username)
			.firstname(firstname)
			.lastname(lastname)
			.email(email)
			.password(password)
			.build();

		if (view == null) {
			Log.d(TAG, String.format("Registering account [%s, %s]", username, email));

			new AsyncTask<Void, Void, Intent>() {

				@Override
				protected Intent doInBackground(final Void... voids) {
					final Token token = backendAuthenticator.signUp(accountDto);
					final Intent res = new Intent();
					res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
					res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
					res.putExtra(AccountManager.KEY_AUTHTOKEN, token.getAuthToken());
					res.putExtra("user_password", password);
					res.putExtra(AccountUtils.KEY_REFRESH_TOKEN, token.getRefreshToken());
					return res;
				}

				@Override
				protected void onPostExecute(final Intent intent) {
					finishLogin(intent);
				}
			}.execute();
		} else {
			view.requestFocus();
		}
	}

	/**
	 * Finish the login procedure. We create an account and register it with the account manager, and signal that we've succeeded.
	 */
	private void finishLogin(final Intent intent) {
		final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		final String accountPassword = intent.getStringExtra("user_password");
		final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
		final String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

		mAccountManager.addAccountExplicitly(account, accountPassword, null);
		mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authtoken);

		sharedPreferences.edit()
			.putString(AccountUtils.PREF_REFRESH_TOKEN, intent.getStringExtra(AccountUtils.KEY_REFRESH_TOKEN))
			.putString(AccountUtils.PREF_AUTH_TOKEN, authtoken)
			.apply();

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	private boolean validateUsername(final String username) {
		return !TextUtils.isEmpty(username) && username.length() > 5;
	}

	private boolean validateEmail(final String email) {
		return !TextUtils.isEmpty(email) && email.contains("@");
	}

	private boolean validatePassword(final String password) {
		return !TextUtils.isEmpty(password) && password.length() > 7;
	}

	private boolean validateMatchingPasswords(final String password, final String passwordRepeated) {
		return password.equals(passwordRepeated);
	}

	private boolean validateFirstname(final String firstname) {
		return !TextUtils.isEmpty(firstname);
	}

	private boolean validateLastname(final String lastname) {
		return !TextUtils.isEmpty(lastname);
	}
}
