package giphouse.nl.proprapp.ui.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.ProprConfiguration;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.dagger.ImageService;
import giphouse.nl.proprapp.service.ImageUtil;
import giphouse.nl.proprapp.service.group.GroupService;
import nl.giphouse.propr.dto.user.UserInfoDto;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class GroupInfoActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_SELECT_IMAGE = 111;

	public static final String ARG_GROUP_ID = "groupId";

	@Inject
	GroupService groupService;

	@Inject
	ProprConfiguration proprConfiguration;

	@Inject
	ImageService imageService;

	private long groupId;

	private GroupUserAdapter adapter;

	private ImageView groupImageView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		if (getIntent() != null && getIntent().getExtras() != null) {
			groupId = getIntent().getExtras().getLong(ARG_GROUP_ID);
		}

		setContentView(R.layout.activity_group_info);

		groupImageView = findViewById(R.id.group_image_view);
		final GridView gridView = findViewById(R.id.users_grid);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}

		adapter = new GroupUserAdapter(getLayoutInflater());
		gridView.setAdapter(adapter);

		groupImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectImage();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		groupService.getUsersInGroup(groupId).enqueue(new Callback<List<UserInfoDto>>() {
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

		imageService.loadGroupImage(groupId).placeholder(R.drawable.placeholder_group).into(groupImageView);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
			if (data != null) {
				updateImage(data);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final int id = item.getItemId();
		if (id == android.R.id.home) {
			final Intent intent = NavUtils.getParentActivityIntent(this);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}
		return true;
	}

	private void updateImage(final @NonNull Intent data) {
		final Uri imageUri = data.getData();
		if (imageUri == null) {
			return;
		}

		final InputStream imageStream;
		try {
			imageStream = getContentResolver().openInputStream(imageUri);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(GroupInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
			return;
		}

		final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
		final byte[] bytes = ImageUtil.getImageBytes(selectedImage, 1500);

		final RequestBody body = RequestBody.create(ImageUtil.JPEG_TYPE, bytes);
		groupService.updateGroupImage(groupId, body).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
				if (response.isSuccessful()) {
					imageService.invalidateGroupImage(groupId);
					groupImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

					Toast.makeText(GroupInfoActivity.this, "Group image updated!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(GroupInfoActivity.this, "Group image not updated!", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {

			}
		});
	}

	private void selectImage() {
		final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
		getIntent.setType("image/*");

		final Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		pickIntent.setType("image/*");

		final Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
		startActivityForResult(Intent.createChooser(chooserIntent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
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
			imageService.loadAccountAvatar(dto.getId()).placeholder(R.drawable.placeholder_avatar).into(avatarImage);

			return itemView;
		}

		void updateItems(final List<UserInfoDto> dtos) {
			users = dtos;
			notifyDataSetChanged();
		}
	}
}
