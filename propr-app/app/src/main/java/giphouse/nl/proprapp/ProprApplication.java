package giphouse.nl.proprapp;

import android.app.Application;

import giphouse.nl.proprapp.dagger.ApiServiceModule;
import giphouse.nl.proprapp.dagger.AppModule;
import giphouse.nl.proprapp.dagger.DaggerProprApplicationComponent;
import giphouse.nl.proprapp.dagger.NetModule;
import giphouse.nl.proprapp.dagger.ProprApplicationComponent;

/**
 * @author haye
 */
public class ProprApplication extends Application {

	private ProprApplicationComponent component;

	@Override
	public void onCreate() {
		super.onCreate();

		component = DaggerProprApplicationComponent.builder()
			.appModule(new AppModule(this))
			.netModule(new NetModule(getString(R.string.backend_url), "app", "secret"))
			.apiServiceModule(new ApiServiceModule())
			.build();
	}

	public ProprApplicationComponent getComponent() {
		return component;
	}
}
