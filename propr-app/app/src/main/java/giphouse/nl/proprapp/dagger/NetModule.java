package giphouse.nl.proprapp.dagger;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.logging.Level;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import giphouse.nl.proprapp.ProprConfiguration;
import giphouse.nl.proprapp.account.BackendAuthenticator;
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
	SharedPreferences provideOkHttpCache(final Application application) {
		return PreferenceManager.getDefaultSharedPreferences(application);
	}

	@Provides
	@Singleton
	Cache providesOkHttpCache(final Application application) {
		return new Cache(application.getCacheDir(), 10 * 1024 * 1024);
	}

	@Provides
	@Singleton
	Gson provideGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		return gsonBuilder.create();
	}

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(final Cache cache, final Application application, final SharedPreferences sharedPreferences, final ProprConfiguration proprConfiguration) {

		final OAuthRequestInterceptor interceptor = new OAuthRequestInterceptor(AccountManager.get(application), sharedPreferences, proprConfiguration);
		final OkHttpClient client = new OkHttpClient.Builder()
			.cache(cache)
			.addInterceptor(interceptor)
			.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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
	BackendAuthenticator provideBackendAuthenticator(final ProprConfiguration proprConfiguration, final OkHttpClient client) {
		return new BackendAuthenticator(proprConfiguration, client);
	}

	@Provides
	@Singleton
	ProprAuthenticator provideAuthenticator(final Application application, final BackendAuthenticator backendAuthenticator) {
		return new ProprAuthenticator(application, backendAuthenticator);
	}

}
