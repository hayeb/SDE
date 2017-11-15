package giphouse.nl.proprapp.ui.group.overview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import giphouse.nl.proprapp.R;

public class GroupTabbedActivity extends AppCompatActivity implements MyTasksFragment.MyTasksInteractionListener, GroupMembersFragment.GroupMembersInteractionListener, ScheduleFragment.ScheduleInteractionListener {

	private static final String TAG = "GroupTabbedActivity";

	private String groupName;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_tabbed);

		groupName = Optional.of(getIntent()).map(Intent::getExtras)
			.map(e -> e.getString("groupname"))
			.orElse(StringUtils.EMPTY);

		setSupportActionBar(findViewById(R.id.toolbar));
		Optional.ofNullable(getSupportActionBar()).ifPresent(tb -> {
			tb.setDisplayHomeAsUpEnabled(true);
			tb.setTitle(groupName);
		});

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		final ViewPager mViewPager = findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		final TabLayout tabLayout = findViewById(R.id.tabs);

		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

		final FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
			.setAction("Action", null).show());
	}


	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_group_tabbed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		SectionsPagerAdapter(final FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(final int position) {
			switch (position) {
				case 0:
					return MyTasksFragment.newInstance(groupName);
				case 1:
					return GroupMembersFragment.newInstance(groupName);
				case 2:
					return ScheduleFragment.newInstance(groupName);
				default:
					throw new RuntimeException("Unhandled index " + position);
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}
	}
}
