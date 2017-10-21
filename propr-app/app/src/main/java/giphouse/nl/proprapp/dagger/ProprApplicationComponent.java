package giphouse.nl.proprapp.dagger;

import javax.inject.Singleton;

import dagger.Component;
import giphouse.nl.proprapp.account.service.AuthenticatorService;
import giphouse.nl.proprapp.account.ui.LoginActivity;
import giphouse.nl.proprapp.account.ui.RegisterAccountActivity;
import giphouse.nl.proprapp.ui.groups.GroupListActivity;

/**
 * @author haye
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class, ApiServiceModule.class})
public interface ProprApplicationComponent {

	void inject(GroupListActivity groupListActivity);

	void inject(LoginActivity loginActivity);

	void inject(RegisterAccountActivity registerAccountActivity);

	void inject(AuthenticatorService authenticatorService);
}
