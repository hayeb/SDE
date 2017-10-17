package giphouse.nl.proprapp.service;

import android.accounts.AccountManager;

import okhttp3.Request;

/**
 * @author haye
 */
public class TempBackendService extends AbstractBackendService {

	private static final String TAG = "TempBackendService";

	public TempBackendService(final AccountManager accountManager, final String backendUrl) {
		super(accountManager, backendUrl);
	}

	public String getBackendMessage()
	{
		final Request.Builder builder = buildBackendCall("/api/test/hello").get();
		return doCall(builder);
	}
}
