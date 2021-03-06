package giphouse.nl.proprapp.dagger;

import android.accounts.AccountManager;
import android.app.Application;
import android.net.Uri;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import giphouse.nl.proprapp.ProprConfiguration;
import giphouse.nl.proprapp.account.AuthenticatorService;
import giphouse.nl.proprapp.account.OAuthRequestInterceptor;
import giphouse.nl.proprapp.account.ProprAuthenticator;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
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
			//.connectTimeout(25, TimeUnit.MINUTES)
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
	Picasso providePicasso(final Application application, final OkHttpClient client) {
		final Picasso.Builder builder = new Picasso.Builder(application);
		builder.downloader(new OkHttp3Downloader(client));
		builder.loggingEnabled(true);
		builder.listener(new Picasso.Listener() {
			@Override
			public void onImageLoadFailed(final Picasso picasso, final Uri uri, final Exception exception) {
				exception.printStackTrace();
			}
		});
		return builder.build();
	}

	@Provides
	@Singleton
	ImageService provideImageService(final Picasso picasso, final ProprConfiguration proprConfiguration) {
		return new ImageServiceImpl(picasso, proprConfiguration);
	}
}
