package giphouse.nl.proprapp.ui.group;

import android.app.ListActivity;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.service.group.search.GroupSearchAdapter;
import giphouse.nl.proprapp.service.group.search.GroupSearchResult;
import giphouse.nl.proprapp.ui.group.overview.GroupOverviewActivity;
import nl.giphouse.propr.dto.group.GroupAddDto;
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
	private Button button;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ProprApplication) getApplication()).getComponent().inject(this);
		setContentView(R.layout.activity_group_join);

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar bar = getSupportActionBar();
		if (bar != null)
		{
			bar.setDisplayHomeAsUpEnabled(true);
		}

		enterGroupcode = findViewById(R.id.enterGroupcode);
		button = findViewById(R.id.button);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				submit();
			}
		});
	}

	private void submit() {
		enterGroupcode.setError(null);

		final String groupCode = enterGroupcode.getText().toString();
		//final String groupname = new String("name");

		if (validateInputShowError(groupCode)) {
			Log.e(TAG, "There was an error..?");
			return;
		}

		groupService.joinGroup(new GroupJoinDto(groupCode)).enqueue(new Callback<GroupDto>() {

			@Override
			public void onResponse(@NonNull final Call<GroupDto> call, @NonNull final Response<GroupDto> response) {
				if (!response.isSuccessful()) {
					final int responseCode = response.code();
					/*if (responseCode == 422) {
						groupNameEdit.setError(getString(R.string.error_groupname_exists));
					} else {
						Log.e(TAG, String.format(getString(R.string.error_unknown_request_error), responseCode, response.message()));
					}*/
				} else {
					Log.i(TAG, "Succesfully created a group");
					final Intent intent = new Intent(GroupJoinActivity.this, GroupOverviewActivity.class);
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
