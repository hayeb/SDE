package giphouse.nl.proprapp.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.service.user.UserService;
import retrofit2.Retrofit;

/**
 * @author haye
 */
@Module
public class ApiServiceModule {

	@Singleton
	@Provides
	public GroupService groupBackendService(final Retrofit retrofit) {
		return retrofit.create(GroupService.class);
	}

	@Singleton
	@Provides
	public UserService userService(final Retrofit retrofit) {
		return retrofit.create(UserService.class);
	}
}
