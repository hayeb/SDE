package giphouse.nl.proprapp.account.task;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.lang3.StringUtils;

import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.AfterAccountCreated;

/**
 * @author haye
 */
public class UserRegisterTask extends AsyncTask<Context, Void, Boolean> {
	private final String mUsername;
	private final String mPassword;
	private final String mEmail;

	private final String mbackendUrl;

	public UserRegisterTask(final String userName, final String password, final String email, final String backendUrl) {
		mUsername = userName;
		mPassword = password;
		mEmail = email;
		mbackendUrl = backendUrl;
	}

	@Override
	protected Boolean doInBackground(final Context... params) {
		final String authToken = AccountUtils.mServerAuthenticator.signUp(mEmail, mUsername, mPassword, mbackendUrl);
		if (StringUtils.isEmpty(authToken)) {
			return false;
		}
		((AfterAccountCreated) params[0]).afterAccountCreated();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
	}
}
