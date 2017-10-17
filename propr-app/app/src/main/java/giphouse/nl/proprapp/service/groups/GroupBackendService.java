package giphouse.nl.proprapp.service.groups;

import android.accounts.AccountManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import giphouse.nl.proprapp.service.AbstractBackendService;
import okhttp3.Request;

/**
 * @author haye
 */
public class GroupBackendService extends AbstractBackendService {

	private final String TAG = "GroupBackendService";

	public GroupBackendService(final AccountManager accountManager, final String backendUrl) {
		super(accountManager, backendUrl);
	}

	public List<GroupDto> getGroupsForUser() {
		final Request.Builder requestBuilder = buildBackendCall("/api/group").get();
		final String response = doCall(requestBuilder);

		Log.d(TAG, "Got response from server: " + response);

		// Hier gebruiken we Gson om JSON om te zetten naar iets bruikbaars. De gekke typeconstructie zorgt ervoor dat om zetten van de lijst goed gaat
		return new Gson().fromJson(response, new TypeToken<List<GroupDto>>() {}.getType());
	}
}
