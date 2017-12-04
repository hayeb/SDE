package giphouse.nl.proprapp.dagger;

import android.accounts.AccountManager;
import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import giphouse.nl.proprapp.ProprConfiguration;
import giphouse.nl.proprapp.account.AuthenticatorService;
import giphouse.nl.proprapp.account.OAuthRequestInterceptor;
import giphouse.nl.proprapp.account.ProprAuthenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author haye
 */
@Module
public class NetModule {
	private final ProprConfiguration proprConfiguration;

	public NetModule(final String backendUrl, final String clientId, final String clientSecret) {
		this.proprConfiguration = new ProprConfiguration(clientId, clientSecret, backendUrl);
	}

	@Provides
	@Singleton
	ProprConfiguration provideProprConfiguration() {
		return proprConfiguration;
	}

	@Provides
	@Singleton
	Cache providesOkHttpCache(final Application application) {
		return new Cache(application.getCacheDir(), Integer.MAX_VALUE);
	}

	@Provides
	@Singleton
	Gson provideGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		gsonBuilder.registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter());
		return gsonBuilder.create();
	}

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(final Cache cache, final Application application, final ProprConfiguration proprConfiguration) {

		final OAuthRequestInterceptor interceptor = new OAuthRequestInterceptor(AccountManager.get(application), proprConfiguration);
		final OkHttpClient client = new OkHttpClient.Builder()
			.cache(cache)
			.addInterceptor(interceptor)
			.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
			.addInterceptor(new Interceptor() {
				@Override
				public Response intercept(@NonNull final Chain chain) throws IOException {
					final Request request = chain.request();
					final Response response = chain.proceed(request);

					if (request.url().toString().endsWith("image") && request.method().equals("GET"))
					{
						final ResponseBody body = response.body();
						final MediaType contentType = body.contentType();
						final byte[] base64Image = Base64.decode(body.string(), Base64.NO_WRAP);
						ResponseBody newBody = ResponseBody.create(contentType, base64Image);
						return response.newBuilder().body(newBody).build();
					} else {
						return response;
					}
				}
			})
			.build();

		interceptor.initHttpClient(client);

		return client;
	}

	@Provides
	@Singleton
	Retrofit provideRetrofit(final Gson gson, final OkHttpClient okHttpClient) {
		return new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create(gson))
			.baseUrl(proprConfiguration.getBackendUrl())
			.client(okHttpClient)
			.build();
	}

	@Provides
	@Singleton
	AuthenticatorService provideBackendAuthenticator(final ProprConfiguration proprConfiguration, final OkHttpClient client) {
		return new AuthenticatorService(proprConfiguration, client);
	}

	@Provides
	@Singleton
	ProprAuthenticator provideAuthenticator(final Application application, final AuthenticatorService authenticatorService) {
		return new ProprAuthenticator(application, authenticatorService);
	}

	@Provides
	@Singleton
	PicassoWrapper providePicasso(final Application application, final OkHttpClient client, final ProprConfiguration proprConfiguration)
	{
		final Picasso.Builder builder = new Picasso.Builder(application);
		builder.downloader(new OkHttp3Downloader(client));
		builder.loggingEnabled(true);

		return new PicassoWrapperImpl(builder.build(), proprConfiguration);
	}
}
