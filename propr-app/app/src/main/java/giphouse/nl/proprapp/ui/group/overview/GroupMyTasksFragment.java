package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.service.task.MyTasksListAdapter;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMyTasksFragment extends ListFragment {

	private static final String TAG = "GroupMyTasksFragment";

	private static final String ARG_PARAM1 = "groupname";

	@Inject
	public TaskService taskService;

	private String groupName;

	private MyTasksInteractionListener mListener;

	private MyTasksListAdapter myTasksListAdapter;

	public static GroupMyTasksFragment newInstance(final String groupName) {
		final GroupMyTasksFragment fragment = new GroupMyTasksFragment();
		final Bundle args = new Bundle();
		args.putString(ARG_PARAM1, groupName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getActivity().getApplication()).getComponent().inject(this);

		if (getArguments() != null) {
			groupName = getArguments().getString(ARG_PARAM1);
		}

		myTasksListAdapter = new MyTasksListAdapter(getLayoutInflater(), this.getContext());
		setListAdapter(myTasksListAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		taskService.getTasksForUserInGroup(groupName).enqueue(new Callback<List<TaskDto>>() {
			@Override
			public void onResponse(@NonNull final Call<List<TaskDto>> call, @NonNull final Response<List<TaskDto>> response) {
				if (response.isSuccessful()) {
					myTasksListAdapter.updateData(response.body());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<TaskDto>> call, @NonNull final Throwable t) {
				Log.e(TAG, "Failed to connect to backend");
				t.printStackTrace();
			}
		});
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof MyTasksInteractionListener) {
			mListener = (MyTasksInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement MyTasksInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface MyTasksInteractionListener {
		// TODO: Update argument type and name
		void onMyTasksInteraction(Uri uri);
	}
}
