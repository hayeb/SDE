package giphouse.nl.proprapp.service;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import giphouse.nl.proprapp.NetworkClientHolder;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * @author haye
 */
public abstract class AbstractBackendService<T> {

	private static final String TAG = "AbstractBackendService";

	protected Request.Builder buildBackendCall(final String url) {

		return new Request.Builder()
				.url(NetworkClientHolder.get().getConfiguration().getBackendUrl() + url);
	}

	protected T doCall(final Request.Builder builder) {
		try {
			final ResponseBody body = NetworkClientHolder.get().getOkHttpClient().newCall(builder.build()).execute().body();
			if (body == null) {
				return null;
			}
			final String content = body.string();
			return new Gson().fromJson(content, getType());
		} catch (final IOException e) {
			return null;
		}
	}

	protected abstract Type getType();
}
