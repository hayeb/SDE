package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import giphouse.nl.proprapp.R;

public class MyTasksFragment extends Fragment {

	private static final String ARG_PARAM1 = "groupname";

	private String groupName;

	private MyTasksInteractionListener mListener;

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
		if (getArguments() != null) {
			groupName = getArguments().getString(ARG_PARAM1);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_my_tasks, container, false);
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
