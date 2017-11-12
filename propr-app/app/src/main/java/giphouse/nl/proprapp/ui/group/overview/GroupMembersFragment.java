package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import giphouse.nl.proprapp.R;

public class GroupMembersFragment extends Fragment {
	private static final String ARG_PARAM1 = "groupname";

	private String groupname;

	private GroupMembersInteractionListener mListener;

	public GroupMembersFragment() {
		// Required empty public constructor
	}

	public static GroupMembersFragment newInstance(final String param1) {
		final GroupMembersFragment fragment = new GroupMembersFragment();
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
		return inflater.inflate(R.layout.fragment_group_members, container, false);
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(final Uri uri) {
		if (mListener != null) {
			mListener.onGroupMembersInteraction(uri);
		}
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof GroupMembersInteractionListener) {
			mListener = (GroupMembersInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement GroupMembersInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface GroupMembersInteractionListener {
		void onGroupMembersInteraction(Uri uri);
	}
}
