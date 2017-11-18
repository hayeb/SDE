package giphouse.nl.proprapp.ui.group.overview;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.commons.lang3.StringUtils;

import giphouse.nl.proprapp.R;
import nl.giphouse.propr.dto.task.TaskDto;

public class GroupOverviewActivity extends AppCompatActivity implements MyTasksFragment.MyTasksInteractionListener, GroupMembersFragment.GroupMembersInteractionListener, ScheduleFragment.ScheduleInteractionListener, GroupActivityFragment.OnGroupActivityFragmentInteractionListener {

	private static final String TAG = "GroupOverviewActivity";

	private String groupName = StringUtils.EMPTY;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_overview);

		if (getIntent() != null && getIntent().getExtras() != null) {
			groupName = getIntent().getExtras().getString("groupname");
		}

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(groupName);
		}

		getSupportFragmentManager().beginTransaction()
			.add(R.id.group_overview_fragment, MyTasksFragment.newInstance(groupName), null)
			.disallowAddToBackStack()
			.commit();

		final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
					item.setChecked(true);
					switch (item.getItemId()) {
						case R.id.item_activity:
							getSupportFragmentManager().beginTransaction()
								.replace(R.id.group_overview_fragment, GroupActivityFragment.newInstance(groupName))
								.disallowAddToBackStack()
								.commit();
							break;
						case R.id.item_schedule:
							getSupportFragmentManager().beginTransaction()
								.replace(R.id.group_overview_fragment, ScheduleFragment.newInstance(groupName))
								.disallowAddToBackStack()
								.commit();
							break;
						case R.id.item_tasks:
							getSupportFragmentManager().beginTransaction()
								.replace(R.id.group_overview_fragment, MyTasksFragment.newInstance(groupName))
								.disallowAddToBackStack()
								.commit();
							break;
					}
					return false;
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.group_overview, menu);
		return true;
	}

	@Override
	public void onScheduleInteraction(final Uri uri) {
		Log.d(TAG, "Schedule interaction");
	}

	@Override
	public void onGroupMembersInteraction(final Uri uri) {
		Log.d(TAG, "Groupmembers interaction");
	}

	@Override
	public void onMyTasksInteraction(final Uri uri) {
		Log.d(TAG, "My Tasks interaction");
	}

	@Override
	public void onGroupActivityFragmentInteraction(TaskDto item) {

	}
}
