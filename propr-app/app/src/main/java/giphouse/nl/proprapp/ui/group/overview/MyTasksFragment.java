package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.task.TaskListAdapter;
import giphouse.nl.proprapp.service.task.TaskService;
import giphouse.nl.proprapp.service.task.UserTaskDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTasksFragment extends ListFragment {

	private static final String TAG = "MyTasksFragment";

	private static final String ARG_PARAM1 = "groupname";

	@Inject
	public TaskService taskService;

	private String groupName;

	private MyTasksInteractionListener mListener;

	private TaskListAdapter taskListAdapter;

	public MyTasksFragment() {
		// Required empty public constructor
	}

	public static MyTasksFragment newInstance(final String groupName) {
		final MyTasksFragment fragment = new MyTasksFragment();
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

		taskListAdapter = new TaskListAdapter(getLayoutInflater(), this.getContext());
		setListAdapter(taskListAdapter);

		taskService.getTasksForUserInGroup(groupName).enqueue(new Callback<List<UserTaskDto>>() {
			@Override
			public void onResponse(@NonNull Call<List<UserTaskDto>> call, @NonNull Response<List<UserTaskDto>> response) {
				if (response.isSuccessful()) {
					taskListAdapter.updateData(response.body());
				} else {
					// Something went wrong..
				}
			}

			@Override
			public void onFailure(@NonNull Call<List<UserTaskDto>> call, @NonNull Throwable t) {
				Log.e(TAG, "Failed to connect to backend");
				t.printStackTrace();
			}
		});
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(final Uri uri) {
		if (mListener != null) {
			mListener.onMyTasksInteraction(uri);
		}
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
