package giphouse.nl.proprapp.service;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * @author haye
 */
public class TempBackendService extends AbstractBackendService {

	private final String TAG = "TempBackendService";

	// TODO: Blegh, try-catch. Misschien een async-library (Retrofit?)
	public String getBackendMessage(final Context context)
	{
		final Request.Builder builder = buildBackendCall("/api/test/hello", context).get();

		final String response;
		try {
			final ResponseBody body = client.newCall(builder.build()).execute().body();
			if (body == null)
			{
				return null;
			}
			response = body.string();
		} catch (final IOException e) {
			Log.d(TAG,"Calling /api/test/hello failed!");
			return null;
		}

		return response;
	}
}
