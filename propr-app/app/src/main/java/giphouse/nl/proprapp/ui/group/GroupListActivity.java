package giphouse.nl.proprapp.ui.group;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.account.AccountUtils;
import giphouse.nl.proprapp.account.AuthenticatorService;
import giphouse.nl.proprapp.account.ui.LoginActivity;
import giphouse.nl.proprapp.dagger.ImageService;
import giphouse.nl.proprapp.service.ImageUtil;
import giphouse.nl.proprapp.service.group.GroupListAdapter;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.service.group.LoadGroupData;
import giphouse.nl.proprapp.service.user.UserService;
import nl.giphouse.propr.dto.user.UserInfoDto;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main layout using a BottomNavigationLayout
 *
 * @author haye
 */
public class GroupListActivity extends ListActivity
	implements NavigationView.OnNavigationItemSelectedListener {
	private static final int REQUEST_CODE_SELECT_IMAGE = 12;

	private static final String TAG = "GroupListActivity";

	@Inject
	GroupService groupService;

	@Inject
	UserService userService;

	@Inject
	ImageService imageService;

	@Inject
	AuthenticatorService authenticatorService;

	private UserInfoDto userInfoDto;

	private TextView accountNameTextView;
	private TextView accountEmailTextView;
	private ImageView accountAvatarView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.ProprTheme);
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		setContentView(R.layout.activity_group_list);

		final AccountManager accountManager = AccountManager.get(this);
		final Account[] accounts = accountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE);

		if (accounts.length == 0) {
			Log.d(TAG, "No account found on device. Starting LoginActivity");
			startActivityForResult(new Intent(this, LoginActivity.class), LoginActivity.CODE_LOGGED_IN);
		} else {
			Log.d(TAG, "Account found on device. Getting additional information");
			final Account account = accounts[0];

			accountManager.getAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, null, false, new AccountManagerCallback<Bundle>() {
				@Override
				public void run(final AccountManagerFuture<Bundle> result) {
					Bundle bundle = null;
					try {
						bundle = result.getResult();
					} catch (OperationCanceledException | IOException | AuthenticatorException ignored) {
					}

					if (bundle == null) {
						Log.e(TAG, "Unable to get auth token from account manager");
						return;
					}
					final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					final String refreshToken = accountManager.getUserData(account, AccountUtils.KEY_REFRESH_TOKEN);
					if (authToken == null || refreshToken == null) {
						Log.i(TAG, "No auth, refresh token found. Starting Login Activity");
						GroupListActivity.this.startActivityForResult(new Intent(GroupListActivity.this, LoginActivity.class), 11);
						return;
					}

					new TokenValidTask(authenticatorService, new WeakReference<>(GroupListActivity.this), account.name).execute(authToken);

				}
			}, null);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (getListAdapter() != null) {
			new LoadGroupData(groupService, (GroupListAdapter) getListAdapter()).execute();
			loadUserInfo(AccountUtils.getUsername(this));
		}
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

		if (id == R.id.nav_manage) {
			// Handle the camera action

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
			updateAvatar(data);
		} else if (requestCode == LoginActivity.CODE_LOGGED_IN && resultCode == RESULT_OK) {
			buildUI();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void updateAvatar(final Intent data) {
		final Uri imageUri = data.getData();
		if (imageUri == null) {
			return;
		}

		final InputStream imageStream;
		try {
			imageStream = getContentResolver().openInputStream(imageUri);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(GroupListActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
			return;
		}

		final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
		final byte[] bytes = ImageUtil.getImageBytes(selectedImage, 500);

		final RequestBody body = RequestBody.create(ImageUtil.JPEG_TYPE, bytes);
		userService.updateUserAvatar(body).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
				if (response.isSuccessful()) {
					imageService.invalidateAccountAvatar(userInfoDto.getId());
					accountAvatarView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

					Toast.makeText(GroupListActivity.this, "Your avatar has been updated!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(GroupListActivity.this, "Updating your avatar failed", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {

			}
		});
	}

	private void selectUserAvatar() {
		final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
		getIntent.setType("image/*");

		final Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		pickIntent.setType("image/*");

		final Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
		startActivityForResult(Intent.createChooser(chooserIntent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
	}

	private void updateUserInfoUI() {
		accountNameTextView.setText(getString(R.string.name_format, userInfoDto.getFirstname(), userInfoDto.getLastname()));
		accountEmailTextView.setText(userInfoDto.getEmail());
		imageService.loadAccountAvatar(userInfoDto.getId()).placeholder(R.drawable.placeholder_avatar).into(accountAvatarView);
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

	private void buildUI() {
		final FloatingActionButton joinGroupAction = findViewById(R.id.join_group);
		joinGroupAction.setIcon(R.drawable.ic_plus);
		joinGroupAction.setTitle("Join a group");
		joinGroupAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				GroupListActivity.this.startActivity(new Intent(GroupListActivity.this, GroupJoinActivity.class));
			}
		});
		final FloatingActionButton createGroupAction = findViewById(R.id.create_group);
		createGroupAction.setTitle("Create a group");
		createGroupAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				GroupListActivity.this.startActivity(new Intent(GroupListActivity.this, GroupAddActivity.class));
			}
		});

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, (Toolbar) findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		final NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		accountNameTextView = navigationView.getHeaderView(0).findViewById(R.id.account_name);
		accountEmailTextView = navigationView.getHeaderView(0).findViewById(R.id.account_email);
		accountAvatarView = navigationView.getHeaderView(0).findViewById(R.id.account_avatar_view);

		accountAvatarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectUserAvatar();
			}
		});

		setListAdapter(new GroupListAdapter(this.getLayoutInflater(), this, imageService));

		new LoadGroupData(groupService, (GroupListAdapter) getListAdapter()).execute();
		loadUserInfo(AccountUtils.getUsername(this));
	}

	private static class TokenValidTask extends AsyncTask<String, Void, Boolean> {
		private final AuthenticatorService authenticatorService;

		private final WeakReference<GroupListActivity> contextReference;

		private final String username;

		TokenValidTask(final AuthenticatorService authenticatorService, final WeakReference<GroupListActivity> contextReference, final String username) {
			this.authenticatorService = authenticatorService;
			this.contextReference = contextReference;
			this.username = username;
		}

		@Override
		protected Boolean doInBackground(final String... tokens) {
			if (tokens == null || tokens.length != 1) {
				throw new IllegalArgumentException("TokenValidTask accepts exactly 1 token as parameter");
			}

			return authenticatorService.tokenValid(tokens[0]);
		}

		@Override
		protected void onPostExecute(final Boolean tokenValid) {
			final GroupListActivity splashActivity = contextReference.get();
			if (splashActivity == null) {
				Log.e(TAG, "No GroupListActivity attached to TokenValidTask");
				return;
			}
			if (!tokenValid) {
				final Intent intent = new Intent(splashActivity, LoginActivity.class);
				intent.putExtra(LoginActivity.LOGIN_REASON_KEY, splashActivity.getString(R.string.expired_login_reason));
				intent.putExtra(LoginActivity.USERNAME_KEY, username);
				splashActivity.startActivityForResult(intent, LoginActivity.CODE_LOGGED_IN);
			} else {
				splashActivity.buildUI();
			}
		}
	}


}
