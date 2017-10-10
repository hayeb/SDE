package giphouse.nl.proprapp.account.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import giphouse.nl.proprapp.account.ProprAuthenticator;

/**
 * @author haye
 */
public class AuthenticatorService extends Service {

	@Override
	public IBinder onBind(final Intent intent) {
		return new ProprAuthenticator(this).getIBinder();
	}
}
