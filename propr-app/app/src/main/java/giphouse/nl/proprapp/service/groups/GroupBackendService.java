package giphouse.nl.proprapp.service.groups;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import giphouse.nl.proprapp.service.AbstractBackendService;
import okhttp3.Request;

/**
 * @author haye
 */
public class GroupBackendService extends AbstractBackendService<List<GroupDto>> {

	private static final String TAG = "GroupBackendService";

	List<GroupDto> getGroups() {
		final Request.Builder requestBuilder = buildBackendCall("/api/group").get();
		return doCall(requestBuilder);
	}

	@Override
	protected Type getType() {
		return new TypeToken<List<GroupDto>>() {
		}.getType();
	}
}
