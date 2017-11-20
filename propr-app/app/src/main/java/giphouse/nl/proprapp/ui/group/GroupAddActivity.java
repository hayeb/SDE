package giphouse.nl.proprapp.ui.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.ui.group.overview.GroupOverviewActivity;
import nl.giphouse.propr.dto.group.GroupAddDto;
import nl.giphouse.propr.dto.group.GroupDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupAddActivity extends AppCompatActivity {

	private static final String TAG = "GroupAddActivity";

	@Inject
	GroupService groupService;

	private TextInputEditText groupNameEdit;
	private TextInputEditText groupCodeEdit;
	private ImageButton groupImageButton;


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);
		setContentView(R.layout.activity_group_add);
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar bar = getSupportActionBar();
		if (bar != null)
		{
			bar.setDisplayHomeAsUpEnabled(true);
		}

		groupNameEdit = findViewById(R.id.editGroupname);
		groupCodeEdit = findViewById(R.id.editGroupcode);
		groupImageButton = findViewById(R.id.groupImageButton);
	}

	private void submit() {
		groupNameEdit.setError(null);
		groupCodeEdit.setError(null);
		final String groupname = groupNameEdit.getText().toString();
		final String groupCode = groupCodeEdit.getText().toString();
		final byte[] image = getImageStream();

		if (validateInputShowError(groupname, groupCode)) {
			Log.e(TAG, "There was an error..?");
			return;
		}

		groupService.createGroup(new GroupAddDto(groupname, groupCode, null)).enqueue(new Callback<GroupDto>() {
			@Override
			public void onResponse(@NonNull final Call<GroupDto> call, @NonNull final Response<GroupDto> response) {
				if (!response.isSuccessful()) {
					final int responseCode = response.code();
					if (responseCode == 422) {
						groupNameEdit.setError(getString(R.string.error_groupname_exists));
					} else {
						Log.e(TAG, String.format(getString(R.string.error_unknown_request_error), responseCode, response.message()));
					}
				} else {
					Log.i(TAG, "Succesfully created a group");
					final Intent intent = new Intent(GroupAddActivity.this, GroupOverviewActivity.class);
					final GroupDto dto = response.body();
					if (dto != null)
					{
						intent.putExtra("groupname", dto.getGroupName());
					}

					startActivity(intent);
				}
			}

			@Override
			public void onFailure(@NonNull final Call<GroupDto> call, @NonNull final Throwable t) {
				Log.e(TAG, "Calling backend failed! OMG!");
				t.printStackTrace();
			}
		});


		// 3. Open the group
	}

	private boolean validateInputShowError(final String groupName, final String groupCode) {
		boolean isError = false;
		if (TextUtils.isEmpty(groupName))
		{
			groupNameEdit.setError(getString(R.string.error_enter_groupname));
			isError = true;
		}

		if (TextUtils.isEmpty(groupCode))
		{
			groupCodeEdit.setError(getString(R.string.error_enter_groupcode));
			isError = true;
		} else if (groupCode.length() < 5) {
			groupCodeEdit.setError(getString(R.string.error_length_groupcode));
			isError = true;
		}

		return isError;
	}

	private byte[] getImageStream() {
		final BitmapDrawable bitmapDrawable = (BitmapDrawable) groupImageButton.getDrawable();
		final Bitmap bitmap = bitmapDrawable.getBitmap();
		final ByteArrayOutputStream ops = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops);
		return ops.toByteArray();
	}
}
