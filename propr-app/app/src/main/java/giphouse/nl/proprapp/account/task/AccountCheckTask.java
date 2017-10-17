package giphouse.nl.proprapp.account.task;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import giphouse.nl.proprapp.account.AccountUtils;

/**
 * @author haye
 */
public class AccountCheckTask extends AsyncTask<Activity, Void, Void> {

    private static final String TAG = "AccountCheckTask";

    private final AccountManager mAccountManager;

    public AccountCheckTask(final AccountManager accountManager) {
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
                if (authToken == null) {
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
