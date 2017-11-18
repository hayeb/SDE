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
 * Shows tasks in a group which are done or overdue.
 *
 * @author haye
 */
public class GroupActivityFragment extends Fragment {

	private static final String ARG_GROUP_NAME = "groupname";

	@Inject
	TaskService taskService;

	private OnGroupActivityFragmentInteractionListener mListener;

	private String groupName;

	public GroupActivityFragment() {
	}

	public static GroupActivityFragment newInstance(final String groupName) {
		final GroupActivityFragment fragment = new GroupActivityFragment();
		final Bundle args = new Bundle();
		args.putString(ARG_GROUP_NAME, groupName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getActivity().getApplication()).getComponent().inject(this);

		if (getArguments() != null) {
			groupName = getArguments().getString(ARG_GROUP_NAME);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		final RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_groupactivity_list, container, false);
		final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);

		view.addItemDecoration(dividerItemDecoration);
		view.setLayoutManager(new LinearLayoutManager(view.getContext()));

		final GroupActivityAdapter adapter = new GroupActivityAdapter(mListener);
		view.setAdapter(adapter);

		taskService.getDoneTasksInGroup(groupName).enqueue(new Callback<List<TaskDto>>() {
			@Override
			public void onResponse(@NonNull final Call<List<TaskDto>> call, @NonNull final Response<List<TaskDto>> response) {
				if (response.isSuccessful()) {
					adapter.updateEntries(response.body());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<TaskDto>> call, @NonNull final Throwable t) {
				Toast.makeText(GroupActivityFragment.this.getContext(), "Unable to connect to server", Toast.LENGTH_LONG).show();
			}
		});


		return view;
	}


	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof OnGroupActivityFragmentInteractionListener) {
			mListener = (OnGroupActivityFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnGroupActivityFragmentInteractionListener {
		// TODO: Update argument type and name
		void onGroupActivityFragmentInteraction(TaskDto item);
	}
}
