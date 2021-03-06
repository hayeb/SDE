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
import giphouse.nl.proprapp.dagger.ImageService;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.ui.group.GroupInfoActivity;
import giphouse.nl.proprapp.ui.group.GroupListActivity;
import giphouse.nl.proprapp.ui.group.overview.GroupMyTasksFragment.MyTasksInteractionListener;
import giphouse.nl.proprapp.ui.group.schedule.GroupScheduleActivity;
import giphouse.nl.proprapp.ui.task.ShowCompletedTaskActivity;
import nl.giphouse.propr.dto.task.TaskDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupOverviewActivity extends AppCompatActivity implements MyTasksInteractionListener, OnGroupTasksFragmentInteractionListener {

	private static final String TAG = "GroupOverviewActivity";

	public static final String ARG_GROUP_NAME = "groupName";

	public static final String ARG_GROUP_ID = "groupId";

	@Inject
	GroupService groupService;

	@Inject
	ImageService imageService;

	private String groupName;

	private Long groupId;

	private Integer selectedItem;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_group_overview);

		if (savedInstanceState != null) {
			groupName = savedInstanceState.getString(ARG_GROUP_NAME);
			groupId = savedInstanceState.getLong(ARG_GROUP_ID);
			selectedItem = savedInstanceState.getInt("selectedItem");
		} else if (getIntent() != null && getIntent().getExtras() != null) {
			groupName = getIntent().getExtras().getString(ARG_GROUP_NAME);
			groupId = getIntent().getExtras().getLong(ARG_GROUP_ID);
		}

		if (selectedItem == null)
		{
			selectedItem = R.id.item_tasks;
		}

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(groupName);
		}

		final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
		setListeners(bottomNavigationView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		switchToItem(selectedItem);
	}

	private void setListeners(final BottomNavigationView bottomNavigationView) {
		bottomNavigationView.setOnNavigationItemSelectedListener(
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
					switchToItem(item.getItemId());
					item.setChecked(true);
					selectedItem = item.getItemId();
					return true;
				}
			});
	}

	private void switchToItem(final Integer itemId)
	{
		if (itemId == null)
		{
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.group_overview_fragment_container, GroupMyTasksFragment.newInstance(groupName))
				.disallowAddToBackStack()
				.commit();
			return;
		}
		switch (itemId) {
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
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putString(ARG_GROUP_NAME, groupName);
		outState.putLong(ARG_GROUP_ID, groupId);
		outState.putInt("selectedItem", selectedItem);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.group_overview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final Intent intent;
		switch (item.getItemId()) {
			case R.id.item_group_info:
				intent = new Intent(this, GroupInfoActivity.class);
				intent.putExtra(GroupInfoActivity.ARG_GROUP_ID, groupId);
				startActivity(intent);
				break;
			case R.id.item_schedule:
				intent = new Intent(this, GroupScheduleActivity.class);
				intent.putExtra(GroupScheduleActivity.ARG_GROUP_ID, groupId);
				intent.putExtra(GroupScheduleActivity.ARG_GROUP_NAME, groupName);
				startActivity(intent);
				break;
			case android.R.id.home:
				intent = NavUtils.getParentActivityIntent(this);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				NavUtils.navigateUpTo(this, intent);
				return true;
			case R.id.item_leave_group:
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
		if (item.getCompletionDate() != null)
		{
			final Intent intent = new Intent(this, ShowCompletedTaskActivity.class);
			final Bundle bundle = new Bundle();
			bundle.putLong(ShowCompletedTaskActivity.ARG_TASK_ID, item.getTaskId());
			bundle.putString(ShowCompletedTaskActivity.ARG_TASK_COMPLETION_NOTES, item.getCompletionNotes());
			bundle.putBoolean(ShowCompletedTaskActivity.ARG_IS_ASSIGNEE, item.isOwned());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public void onGroupScheduleFragmentInteraction(final TaskDto item) {

	}

	@Override
	public void onPointerCaptureChanged(final boolean hasCapture) {

	}
}
