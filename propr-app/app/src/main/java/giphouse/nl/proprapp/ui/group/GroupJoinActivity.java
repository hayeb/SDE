package giphouse.nl.proprapp.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.ui.group.overview.GroupOverviewActivity;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.group.GroupJoinDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupJoinActivity extends AppCompatActivity {

	private static final String TAG = "GroupJoinActivity";

	@Inject
	GroupService groupService;

	private TextInputEditText enterGroupcode;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ProprApplication) getApplication()).getComponent().inject(this);
		setContentView(R.layout.activity_group_join);

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}

		enterGroupcode = findViewById(R.id.enterGroupcode);

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				submit();
			}
		});
	}

	private void submit() {
		enterGroupcode.setError(null);

		final String groupCode = enterGroupcode.getText().toString();

		if (validateInputShowError(groupCode)) {
			return;
		}

		groupService.joinGroup(new GroupJoinDto(groupCode)).enqueue(new Callback<GroupDto>() {

			@Override
			public void onResponse(@NonNull final Call<GroupDto> call, @NonNull final Response<GroupDto> response) {
				if (!response.isSuccessful()) {
					switch (response.code()) {
						case 422:
							// Group not found
							Toast.makeText(GroupJoinActivity.this, "No group found with this code!", Toast.LENGTH_LONG).show();
							break;
						case 400:
							// Already part of the group
							Toast.makeText(GroupJoinActivity.this, "You are already in this group!", Toast.LENGTH_LONG).show();
						default:
							Toast.makeText(GroupJoinActivity.this, "Unknown error joining group!", Toast.LENGTH_LONG).show();
					}
					return;
				}
				final Intent intent = new Intent(GroupJoinActivity.this, GroupOverviewActivity.class);
				final GroupDto dto = response.body();
				if (dto != null) {
					intent.putExtra(GroupOverviewActivity.ARG_GROUP_NAME, dto.getGroupName());
					intent.putExtra(GroupOverviewActivity.ARG_GROUP_ID, dto.getGroupId());
				}

				startActivity(intent);
			}

			@Override
			public void onFailure(@NonNull final Call<GroupDto> call, @NonNull final Throwable t) {
				Log.e(TAG, "Calling backend failed! OMG!");
				t.printStackTrace();
			}
		});
	}

	private boolean validateInputShowError(final String groupCode) {
		boolean isError = false;
		if (TextUtils.isEmpty(groupCode)) {
			enterGroupcode.setError(getString(R.string.error_enter_groupcode));
			isError = true;
		} else if (groupCode.length() < 5) {
			enterGroupcode.setError(getString(R.string.error_length_groupcode));
			isError = true;
		}
		return isError;
	}
}
