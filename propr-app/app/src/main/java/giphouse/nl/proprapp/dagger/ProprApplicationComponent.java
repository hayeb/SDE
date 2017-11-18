package giphouse.nl.proprapp.dagger;

import javax.inject.Singleton;

import dagger.Component;
import giphouse.nl.proprapp.SplashActivity;
import giphouse.nl.proprapp.account.service.AuthenticatorService;
import giphouse.nl.proprapp.account.ui.LoginActivity;
import giphouse.nl.proprapp.account.ui.RegisterAccountActivity;
import giphouse.nl.proprapp.ui.group.GroupAddActivity;
import giphouse.nl.proprapp.ui.group.GroupJoinActivity;
import giphouse.nl.proprapp.ui.group.GroupListActivity;
import giphouse.nl.proprapp.ui.group.overview.GroupActivityFragment;
import giphouse.nl.proprapp.ui.group.overview.GroupMembersFragment;
import giphouse.nl.proprapp.ui.group.overview.GroupMyTasksFragment;
import giphouse.nl.proprapp.ui.group.overview.GroupScheduleFragment;

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

	void inject(GroupAddActivity groupAddActivity);

	void inject(GroupJoinActivity groupJoinActivity);

	void inject(GroupMyTasksFragment myTasksFragment);

	void inject(SplashActivity splashActivity);

	void inject(GroupMembersFragment groupMembersFragment);

	void inject(GroupActivityFragment groupActivityFragment);

	void inject(GroupScheduleFragment groupScheduleFragment);
}
