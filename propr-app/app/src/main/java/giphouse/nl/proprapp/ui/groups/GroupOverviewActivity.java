package giphouse.nl.proprapp.ui.groups;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;

public class GroupOverviewActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "GroupOverviewActivity" ;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_overview);

		new AccountCheckTask(AccountManager.get(this)).execute();
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {

	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {

	}

	protected static class AccountCheckTask extends AsyncTask<Activity, Void, Void>
	{
		private final AccountManager mAccountManager;

		AccountCheckTask(final AccountManager accountManager)
		{
			this.mAccountManager = accountManager;
		}

		@Override
		protected Void doInBackground(final Activity... activity) {
			mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, activity[0], null, null, result -> {
				final Bundle bundle;
				try {
					bundle = result.getResult();

					// An intent is returned: Start it to acquire proper account credentials
					final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
					if (intent != null) {
						activity[0].startActivityForResult(intent, 1);
					}

					final String authToken = (String) bundle.get(AccountManager.KEY_AUTHTOKEN);
					if (authToken == null)
					{
						Log.i(TAG, "Account present, but no auth token: Ask for credentials");
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}, null);
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {

		}
	}

}
