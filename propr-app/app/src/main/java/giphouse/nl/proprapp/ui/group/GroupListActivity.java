package giphouse.nl.proprapp.ui.group;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.service.group.GroupListAdapter;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.service.group.LoadGroupData;
import giphouse.nl.proprapp.service.user.UserInfoDto;
import giphouse.nl.proprapp.service.user.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main layout using a DrawerLayout (https://developer.android.com/training/implementing-navigation/nav-drawer.html#DrawerLayout)
 *
 * @author haye
 */
public class GroupListActivity extends ListActivity
	implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "GroupListActivity";

	@Inject
	GroupService groupService;

	@Inject
	UserService userService;

	private UserInfoDto userInfoDto;

	private TextView accountNameTextView;
	private TextView accountEmailTextView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_group_list);

		final FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> startActivity(new Intent(this, GroupAddActivity.class)));

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		final NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		accountNameTextView = navigationView.getHeaderView(0).findViewById(R.id.account_name);
		accountEmailTextView = navigationView.getHeaderView(0).findViewById(R.id.account_email);

		setListAdapter(new GroupListAdapter(this.getLayoutInflater(), this));

		final LoadGroupData loadGroupData = new LoadGroupData(groupService, (GroupListAdapter) getListAdapter());
		loadGroupData.execute();

		loadUserInfo(AccountUtils.getUsername(this));
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Reload the group list overview
		new LoadGroupData(groupService, (GroupListAdapter) getListAdapter()).execute();

	}

	@Override
	public void onBackPressed() {
		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
		// Handle navigation view item clicks here.
		final int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {

	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {

	}

	private void updateUserInfoUI() {
		accountNameTextView.setText(getString(R.string.name_format, userInfoDto.getFirstname(), userInfoDto.getLastname()));
		accountEmailTextView.setText(userInfoDto.getEmail());
	}

	private void loadUserInfo(final String username) {
		if (TextUtils.isEmpty(username)) {
			Log.i(TAG, "Not retrieving user data. Username is emtpy.");
			return;
		}
		userService.getUserInfo(username).enqueue(new Callback<UserInfoDto>() {
			@Override
			public void onResponse(@NonNull final Call<UserInfoDto> call, @NonNull final Response<UserInfoDto> response) {
				if (response.isSuccessful()) {
					userInfoDto = response.body();
					updateUserInfoUI();
				} else {
					switch (response.code()) {
						case 404:
							Log.i(TAG, String.format("User %s not found.", username));
							break;
						default:
							Log.e(TAG, String.format(getString(R.string.error_unknown_request_error), response.code(), response.message()));
					}
				}
			}

			@Override
			public void onFailure(@NonNull final Call<UserInfoDto> call, @NonNull final Throwable t) {
				t.printStackTrace();
				Log.e(TAG, "Unable to get user data.");
			}
		});
	}
}
