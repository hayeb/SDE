package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Show the tasks still to be done in the future in a group.
 *
 * @author haye
 */
public class GroupScheduleFragment extends Fragment {

	private static final String ARG_PARAM1 = "groupname";

	@Inject
	TaskService taskService;

	private String groupName;

	private OnGroupTasksFragmentInteractionListener mListener;

	public GroupScheduleFragment() {
		// Required empty public constructor
	}

	public static GroupScheduleFragment newInstance(final String param1) {
		final GroupScheduleFragment fragment = new GroupScheduleFragment();
		final Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
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
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {

		final RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_groupschedule_list, container, false);
		final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);

		view.addItemDecoration(dividerItemDecoration);
		view.setLayoutManager(new LinearLayoutManager(view.getContext()));

		final GroupTasksAdapter adapter = new GroupTasksAdapter(mListener);
		view.setAdapter(adapter);

		taskService.getTodoTasksInGroup(groupName).enqueue(new Callback<List<TaskDto>>() {
			@Override
			public void onResponse(@NonNull final Call<List<TaskDto>> call, @NonNull final Response<List<TaskDto>> response) {
				if (response.isSuccessful()) {
					adapter.updateEntries(response.body());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<TaskDto>> call, @NonNull final Throwable t) {
				Toast.makeText(GroupScheduleFragment.this.getContext(), "Unable to connect to server", Toast.LENGTH_LONG).show();
			}
		});


		return view;
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof OnGroupTasksFragmentInteractionListener) {
			mListener = (OnGroupTasksFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement ScheduleInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
}
