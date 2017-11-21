package giphouse.nl.proprapp.ui.group.overview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.ui.group.GroupMembersActivity;
import giphouse.nl.proprapp.ui.group.overview.GroupMyTasksFragment.MyTasksInteractionListener;
import nl.giphouse.propr.dto.task.TaskDto;

public class GroupOverviewActivity extends AppCompatActivity implements MyTasksInteractionListener, OnGroupTasksFragmentInteractionListener {

	private static final String TAG = "GroupOverviewActivity";

	private String groupName;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_group_overview);

		if (savedInstanceState != null) {
			groupName = savedInstanceState.getString("groupname");
		} else if (getIntent() != null && getIntent().getExtras() != null) {
			groupName = getIntent().getExtras().getString("groupname");
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
		if (item.getItemId() == R.id.item_members) {
			final Intent intent = new Intent(this, GroupMembersActivity.class);

			intent.putExtra(GroupMembersActivity.ARG_PARAM1, groupName);
			startActivity(intent);
		} else if (item.getItemId() == android.R.id.home) {
			final Intent intent = NavUtils.getParentActivityIntent(this);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMyTasksInteraction(final Uri uri) {
		Log.e(TAG, "My Tasks interaction");
	}

	@Override
	public void onGroupActivityFragmentInteraction(final TaskDto item) {

	}

	@Override
	public void onGroupScheduleFragmentInteraction(final TaskDto item) {

	}
}
