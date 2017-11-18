package giphouse.nl.proprapp.ui.group.overview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import lombok.AllArgsConstructor;
import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.dto.user.UserInfoDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: Move to a separate activity
public class GroupMembersFragment extends Fragment {
	private static final String ARG_PARAM1 = "groupname";

	@Inject
	GroupService groupService;

	private String groupname;

	private GroupMembersInteractionListener mListener;

	private GroupUserAdapter adapter;

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

		((ProprApplication)getActivity().getApplication()).getComponent().inject(this);

		adapter = new GroupUserAdapter(getLayoutInflater());
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_group_members, container, false);

		final GridView gridView = view.findViewById(R.id.users_grid);
		gridView.setAdapter(adapter);

		groupService.getUsersInGroup(groupname).enqueue(new Callback<List<UserInfoDto>>() {
			@Override
			public void onResponse(@NonNull final Call<List<UserInfoDto>> call, @NonNull final Response<List<UserInfoDto>> response) {
				if (response.isSuccessful())
				{
					adapter.updateItems(response.body());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<UserInfoDto>> call, @NonNull final Throwable t) {

			}
		});

		return view;
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

	private class GroupUserAdapter extends BaseAdapter
	{
		private List<UserInfoDto> users = new ArrayList<>();

		private final LayoutInflater layoutInflater;

		public GroupUserAdapter(final LayoutInflater layoutInflater)
		{
			this.layoutInflater = layoutInflater;
		}

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(final int position) {
			return users.get(position);
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		@Override
		public View getView(final int position, final View view, final ViewGroup parent) {
			final ConstraintLayout itemView;
			if (view == null) {
				itemView = (ConstraintLayout) layoutInflater.inflate(
					R.layout.item_user, parent, false);

			} else {
				itemView = (ConstraintLayout) view;
			}

			final UserInfoDto dto = users.get(position);

			final TextView usernameText = itemView.findViewById(R.id.item_username);
			usernameText.setText(dto.getUsername());

			final ImageView avatarImage = itemView.findViewById(R.id.account_avatar);
			avatarImage.setImageResource(R.drawable.placeholder_avatar);

			return itemView;
		}

		public void updateItems(final List<UserInfoDto> dtos) {
			users = dtos;
			notifyDataSetChanged();
		}

	}
}
