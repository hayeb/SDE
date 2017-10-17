package giphouse.nl.proprapp.service.groups;

import android.os.AsyncTask;

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
		return groupBackendService.getGroupsForUser();
	}

	@Override
	protected void onPostExecute(final List<GroupDto> groupDtos) {
		super.onPostExecute(groupDtos);
		groupListAdapter.updateEntries(groupDtos);
	}
}
