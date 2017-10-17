package giphouse.nl.proprapp.ui.groups;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.TempBackendService;

/**
 * Tijdelijke activity om communicatie met backend te laten zien
 * @author haye
 */
public class TempActivity extends Activity {

	private TextView text;

	private AccountManager accountManager;

	@SuppressLint("StaticFieldLeak")
	@Override
	protected void onCreate(final Bundle savedInstane) {
		super.onCreate(savedInstane);
		setContentView(R.layout.activity_temp);

		text = findViewById(R.id.backend_response_text);
		accountManager = AccountManager.get(this);

		new AsyncTask<Void, Void, String>()
		{

			@Override
			protected String doInBackground(final Void... voids) {
				return new TempBackendService().getBackendMessage(TempActivity.this);
			}

			// Wordt uitgevoerd op het UI-thread, dus we mogen het text element updaten.
			@Override
			protected void onPostExecute(final String s) {
				handleIncomingMessage(s);
			}
		}.execute();
	}

	private void handleIncomingMessage(final String message)
	{
		if (message != null)
		{
			text.setText(message);
		}
	}
}
