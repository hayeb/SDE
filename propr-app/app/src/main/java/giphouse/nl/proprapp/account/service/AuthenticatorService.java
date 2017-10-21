package giphouse.nl.proprapp.account.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.account.ProprAuthenticator;

/**
 * @author haye
 */
public class AuthenticatorService extends Service {

	@Inject
	ProprAuthenticator proprAuthenticator;

	@Override
	public void onCreate() {
		super.onCreate();

		((ProprApplication) getApplication()).getComponent().inject(this);
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return proprAuthenticator.getIBinder();
	}
}
