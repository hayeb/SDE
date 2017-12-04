package giphouse.nl.proprapp.ui.group.overview;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.ui.group.GroupInfoActivity;
import giphouse.nl.proprapp.ui.group.GroupListActivity;
import giphouse.nl.proprapp.ui.group.overview.GroupMyTasksFragment.MyTasksInteractionListener;
import giphouse.nl.proprapp.ui.task.ShowCompletedTaskActivity;
import nl.giphouse.propr.dto.task.TaskDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupOverviewActivity extends AppCompatActivity implements MyTasksInteractionListener, OnGroupTasksFragmentInteractionListener {

	private static final String TAG = "GroupOverviewActivity";

	@Inject
	GroupService groupService;

	private String groupName;

	private Long groupId;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_group_overview);

		if (savedInstanceState != null) {
			groupName = savedInstanceState.getString("groupname");
			groupId = savedInstanceState.getLong("groupId");
		} else if (getIntent() != null && getIntent().getExtras() != null) {
			groupName = getIntent().getExtras().getString("groupname");
			groupId = getIntent().getExtras().getLong("groupId");
		}

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(groupName);
		}

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.group_overview_fragment_container, GroupMyTasksFragment.newInstance(groupName), null)
			.disallowAddToBackStack()
			.commit();

		final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
		bottomNavigationView.getMenu().getItem(0).setChecked(true);

		bottomNavigationView.setOnNavigationItemSelectedListener(
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
					item.setChecked(true);
					switch (item.getItemId()) {
						case R.id.item_activity:
							getSupportFragmentManager().beginTransaction()
								.replace(R.id.group_overview_fragment_container, GroupActivityFragment.newInstance(groupName))
								.disallowAddToBackStack()
								.commit();
							break;
						case R.id.item_schedule:
							getSupportFragmentManager().beginTransaction()
								.replace(R.id.group_overview_fragment_container, GroupScheduleFragment.newInstance(groupName))
								.disallowAddToBackStack()
								.commit();
							break;
						case R.id.item_tasks:
							getSupportFragmentManager().beginTransaction()
								.replace(R.id.group_overview_fragment_container, GroupMyTasksFragment.newInstance(groupName))
								.disallowAddToBackStack()
								.commit();
							break;
					}
					return false;
				}
			});
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putString("groupname", groupName);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		groupName = savedInstanceState.getString("groupname");
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.group_overview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.id.item_group_info) {
			final Intent intent = new Intent(this, GroupInfoActivity.class);

			intent.putExtra(GroupInfoActivity.ARG_GROUP_ID, groupId);
			startActivity(intent);
		} else if (item.getItemId() == android.R.id.home) {
			final Intent intent = NavUtils.getParentActivityIntent(this);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			NavUtils.navigateUpTo(this, intent);
			return true;
		} else if (item.getItemId() == R.id.item_leave_group) {
			leaveGroup();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void leaveGroup() {
		new AlertDialog.Builder(this)
			.setMessage(R.string.message_leave_group)
			.setPositiveButton(R.string.label_leave_group, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					groupService.leaveGroup(groupId).enqueue(new Callback<Void>() {
						@Override
						public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
							if (response.isSuccessful()) {
								final Intent intent = new Intent(GroupOverviewActivity.this, GroupListActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_SINGLE_TOP);
								NavUtils.navigateUpTo(GroupOverviewActivity.this, intent);
							} else {
								Toast.makeText(GroupOverviewActivity.this, "Error leaving group!", Toast.LENGTH_LONG).show();
							}
						}

						@Override
						public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
							Toast.makeText(GroupOverviewActivity.this, "Unable to contact server.", Toast.LENGTH_LONG).show();
						}
					});
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {

				}
			}).create().show();
	}

	@Override
	public void onMyTasksInteraction(final Uri uri) {
		Log.e(TAG, "My Tasks interaction");
	}

	@Override
	public void onGroupActivityFragmentInteraction(final TaskDto item) {
		final Intent intent = new Intent(this, ShowCompletedTaskActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putLong(ShowCompletedTaskActivity.ARG_TASK_ID, item.getTaskId());
		bundle.putString(ShowCompletedTaskActivity.ARG_TASK_COMPLETION_NOTES, item.getCompletionNotes());
		// TODO: Pass isAssignee parameter
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onGroupScheduleFragmentInteraction(final TaskDto item) {

	}
}
