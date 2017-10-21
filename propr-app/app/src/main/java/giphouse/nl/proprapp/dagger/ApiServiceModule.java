package giphouse.nl.proprapp.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import giphouse.nl.proprapp.service.groups.GroupBackendService;
import retrofit2.Retrofit;

/**
 * @author haye
 */
@Module
public class ApiServiceModule {

	@Singleton
	@Provides
	public GroupBackendService groupBackendService(final Retrofit retrofit) {
		return retrofit.create(GroupBackendService.class);
	}
}
