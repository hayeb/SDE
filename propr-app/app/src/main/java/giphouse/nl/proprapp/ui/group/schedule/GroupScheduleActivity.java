package giphouse.nl.proprapp.ui.group.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.ui.group.TaskDefinitionActivity;
import nl.giphouse.propr.dto.group.GenerateScheduleDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class GroupScheduleActivity extends AppCompatActivity {

	public static final String ARG_GROUP_ID = "groupId";
	public static final String ARG_GROUP_NAME = "groupName";

	@Inject
	GroupService groupService;

	private Long groupId;
	private String groupName;

	private ExpandableListView listView;

	@Override
	protected void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		final Intent intent = getIntent();
		if (savedInstanceState != null) {
			groupId = savedInstanceState.getLong(ARG_GROUP_ID);
			groupName = savedInstanceState.getString(ARG_GROUP_NAME);
		} else if (intent != null && intent.getExtras() != null) {
			groupId = intent.getExtras().getLong(ARG_GROUP_ID);
			groupName = intent.getExtras().getString(ARG_GROUP_NAME);
		}

		setContentView(R.layout.activity_group_schedule);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null){
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle("Schedule for '" + groupName + "'");
		}

		listView = findViewById(R.id.group_schedule_list);
	}

	@Override
	protected void onResume() {
		super.onResume();

		groupService.getGroupSchedule(groupId).enqueue(new Callback<Map<TaskRepetitionType, List<TaskDefinitionDto>>>() {
			@Override
			public void onResponse(@NonNull final Call<Map<TaskRepetitionType, List<TaskDefinitionDto>>> call, @NonNull final Response<Map<TaskRepetitionType, List<TaskDefinitionDto>>> response) {
				if (response.isSuccessful()) {
					listView.setAdapter(new GroupScheduleAdapter(GroupScheduleActivity.this, response.body()));
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Map<TaskRepetitionType, List<TaskDefinitionDto>>> call, @NonNull final Throwable t) {

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_group_schedule, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home:
				intent = NavUtils.getParentActivityIntent(this);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				NavUtils.navigateUpTo(this, intent);
				return true;
			case R.id.add_task:
				intent = new Intent(this, TaskDefinitionActivity.class);
				intent.putExtra("groupId", groupId);
				intent.putExtra("groupName", groupName);
				startActivity(intent);
			case R.id.update_schedule:
				groupService.rescheduleGroup(groupId, new GenerateScheduleDto(365)).enqueue(new Callback<Void>() {
					@Override
					public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
						if (!response.isSuccessful()) {
							Toast.makeText(GroupScheduleActivity.this, "Updating schedule failed.", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(GroupScheduleActivity.this, "Succesfully updated the schedule!", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
						Toast.makeText(GroupScheduleActivity.this, "Unable to connect to server.", Toast.LENGTH_LONG).show();
					}
				});
		}
		return super.onOptionsItemSelected(item);
	}

	public void editTask(final TaskDefinitionDto definitionDto)
	{
		final Intent intent = new Intent(this, TaskDefinitionActivity.class);
		intent.putExtra(TaskDefinitionActivity.ARG_GROUP_ID, groupId);
		intent.putExtra(TaskDefinitionActivity.ARG_GROUP_NAME, groupName);
		intent.putExtra(TaskDefinitionActivity.ARG_DEFINITION_ID, definitionDto.getDefinitionId());
		intent.putExtra(TaskDefinitionActivity.ARG_DEFINITION_NAME, definitionDto.getName());
		intent.putExtra(TaskDefinitionActivity.ARG_DEFINITION_DESCRIPTION, definitionDto.getDescription());
		intent.putExtra(TaskDefinitionActivity.ARG_DEFINITION_FREQUENCY, definitionDto.getFrequency());
		intent.putExtra(TaskDefinitionActivity.ARG_DEFINITION_FREQUENCY_TYPE, definitionDto.getPeriodType().name());
		intent.putExtra(TaskDefinitionActivity.ARG_DEFINITION_WEIGHT, definitionDto.getWeight().name());
		startActivity(intent);
	}
}
