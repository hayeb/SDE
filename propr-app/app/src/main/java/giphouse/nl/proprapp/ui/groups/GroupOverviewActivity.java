package giphouse.nl.proprapp.ui.groups;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;

public class GroupOverviewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_overview);
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show());

		final AccountManager accountManager = AccountManager.get(this);
		accountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, result -> {
			final Bundle bundle;
			try {
				bundle = result.getResult();

				// An intent is returned: Start it to acquire proper account credentials
				final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					startActivityForResult(intent, 1);
				}

				final String authToken = (String) bundle.get(AccountManager.KEY_AUTHTOKEN);
				if (authToken == null)
				{

				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}, null);
	}
}
