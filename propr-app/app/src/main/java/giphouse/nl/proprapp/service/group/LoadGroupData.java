package giphouse.nl.proprapp.service.group;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import giphouse.nl.proprapp.service.group.model.GroupListItemDto;

/**
 * @author haye
 */
public class LoadGroupData extends AsyncTask<Void, Void, List<GroupListItemDto>> {

	private final GroupService groupService;

	private final GroupListAdapter groupListAdapter;

	public LoadGroupData(final GroupService groupService, final GroupListAdapter groupListAdapter) {
		this.groupService = groupService;
		this.groupListAdapter = groupListAdapter;
	}

	@Override
	protected List<GroupListItemDto> doInBackground(final Void... voids) {
		try {
			final List<GroupListItemDto> dtos = groupService.listGroups().execute().body();
			if (dtos == null) {
				Log.e("LoadGroupData", "No response from server!");
				return Collections.emptyList();
			}
			return dtos;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	protected void onPostExecute(final List<GroupListItemDto> groupListItemDtos) {
		super.onPostExecute(groupListItemDtos);
		groupListAdapter.updateEntries(groupListItemDtos);
	}
}
