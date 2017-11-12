package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import giphouse.nl.proprapp.R;

public class ScheduleFragment extends Fragment {

	private static final String ARG_PARAM1 = "groupname";

	private String groupname;

	private ScheduleInteractionListener mListener;

	public ScheduleFragment() {
		// Required empty public constructor
	}

	public static ScheduleFragment newInstance(final String param1) {
		final ScheduleFragment fragment = new ScheduleFragment();
		final Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			groupname = getArguments().getString(ARG_PARAM1);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_schedule, container, false);
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(final Uri uri) {
		if (mListener != null) {
			mListener.onScheduleInteraction(uri);
		}
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof ScheduleInteractionListener) {
			mListener = (ScheduleInteractionListener) context;
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

	public interface ScheduleInteractionListener {

		void onScheduleInteraction(Uri uri);
	}
}
