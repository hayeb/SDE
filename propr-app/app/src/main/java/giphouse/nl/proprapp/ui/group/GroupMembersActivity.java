package giphouse.nl.proprapp.ui.group;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import nl.giphouse.propr.dto.user.UserInfoDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class GroupMembersActivity extends AppCompatActivity {
	public static final String ARG_PARAM1 = "groupname";

	@Inject
	GroupService groupService;

	private String groupname;

	private GroupUserAdapter adapter;

	public GroupMembersActivity() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		groupname = getIntent().getStringExtra(ARG_PARAM1);

		setContentView(R.layout.activity_group_members);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}
		adapter = new GroupUserAdapter(getLayoutInflater());

		final GridView gridView = findViewById(R.id.users_grid);
		gridView.setAdapter(adapter);

		groupService.getUsersInGroup(groupname).enqueue(new Callback<List<UserInfoDto>>() {
			@Override
			public void onResponse(@NonNull final Call<List<UserInfoDto>> call, @NonNull final Response<List<UserInfoDto>> response) {
				if (response.isSuccessful()) {
					adapter.updateItems(response.body());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<UserInfoDto>> call, @NonNull final Throwable t) {

			}
		});
	}

	private class GroupUserAdapter extends BaseAdapter {
		private List<UserInfoDto> users = new ArrayList<>();

		private final LayoutInflater layoutInflater;

		GroupUserAdapter(final LayoutInflater layoutInflater) {
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

		void updateItems(final List<UserInfoDto> dtos) {
			users = dtos;
			notifyDataSetChanged();
		}

	}
}
