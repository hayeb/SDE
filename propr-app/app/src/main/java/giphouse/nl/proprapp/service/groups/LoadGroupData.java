package giphouse.nl.proprapp.service.groups;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author haye
 */
public class LoadGroupData extends AsyncTask<Void, Void, List<GroupDto>> {

	private final GroupBackendService groupBackendService;

	private final GroupListAdapter groupListAdapter;

	public LoadGroupData(final GroupBackendService groupBackendService, final GroupListAdapter groupListAdapter) {
		this.groupBackendService = groupBackendService;
		this.groupListAdapter = groupListAdapter;
	}

	@Override
	protected List<GroupDto> doInBackground(final Void... voids) {
		try {
			final List<GroupDto> dtos = groupBackendService.listGroups().execute().body();
			if (dtos == null) {
				Log.e("LoadGroupData", "No response from server!");
				return Collections.emptyList();
			}
			return groupBackendService.listGroups().execute().body();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	protected void onPostExecute(final List<GroupDto> groupDtos) {
		super.onPostExecute(groupDtos);
		groupListAdapter.updateEntries(groupDtos);
	}
}
