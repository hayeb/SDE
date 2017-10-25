package giphouse.nl.proprapp.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author haye
 */
@Module
public class AppModule {

	private final Application application;

	public AppModule(final Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	Application providesApplication() {
		return application;
	}
}
