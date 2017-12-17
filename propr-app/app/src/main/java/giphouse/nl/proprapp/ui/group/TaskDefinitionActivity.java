package giphouse.nl.proprapp.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.group.GenerateScheduleDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDefinitionActivity extends AppCompatActivity {
	public static final String ARG_GROUP_NAME = "groupName";
	public static final String ARG_GROUP_ID = "groupId";

	public static final String ARG_DEFINITION_ID = "definitionId";
	public static final String ARG_DEFINITION_NAME = "definitionName";
	public static final String ARG_DEFINITION_DESCRIPTION = "definitionDescription";
	public static final String ARG_DEFINITION_FREQUENCY = "definitionFrequency";
	public static final String ARG_DEFINITION_FREQUENCY_TYPE = "definitionFrequencyType";
	public static final String ARG_DEFINITION_WEIGHT = "definitionWeight";

	private static final String TAG = "TaskDefinitionActivity";

	@Inject
	TaskService taskService;

	@Inject
	GroupService groupService;

	private Spinner frequencyTypeSpinner;
	private Spinner taskWeightSpinner;
	private TextInputEditText enterName;
	private TextInputEditText enterDescription;
	private TextInputEditText enterNumber;
	private long groupId;
	private String groupName;
	private Long definitionId;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ProprApplication) getApplication()).getComponent().inject(this);
		setContentView(R.layout.activity_add_task);

		if (getIntent() != null && getIntent().getExtras() != null) {
			groupName = getIntent().getExtras().getString(ARG_GROUP_NAME);
			groupId = getIntent().getExtras().getLong(ARG_GROUP_ID);
		}

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}

		enterName = findViewById(R.id.enterTaskName);
		enterDescription = findViewById(R.id.enterDescription);
		enterNumber = findViewById(R.id.enterNumber);

		final Button doneButton = findViewById(R.id.button_done);
		final Button nextButton = findViewById(R.id.button_next);
		final Button cancelButton = findViewById(R.id.button_cancel);
		final Button saveButton = findViewById(R.id.button_save);

		saveButton.setVisibility(View.GONE);

		frequencyTypeSpinner = findViewById(R.id.spinner);
		final ArrayAdapter<CharSequence> frequencyTypeAdapter = ArrayAdapter
			.createFromResource(this, R.array.frequencytypes,
				android.R.layout.simple_spinner_item);
		frequencyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		frequencyTypeSpinner.setAdapter(frequencyTypeAdapter);

		taskWeightSpinner = findViewById(R.id.spinnerWeight);
		final ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter
			.createFromResource(this, R.array.weights,
				android.R.layout.simple_spinner_item);
		weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		taskWeightSpinner.setAdapter(weightAdapter);

		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				submit();
				reschedule();
			}
		});

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				submit();
				final Intent intent = new Intent(v.getContext(), TaskDefinitionActivity.class);
				intent.putExtra(TaskDefinitionActivity.ARG_GROUP_ID, groupId);
				intent.putExtra(TaskDefinitionActivity.ARG_GROUP_NAME, groupName);
				startActivity(intent);
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				navigateToParent();
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				submit();
				if (needsRescheduling())
				{
					reschedule();
				}
				else {
					navigateToParent();
				}
			}
		});

		final Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(ARG_DEFINITION_ID))
		{
			// We are edition a existing definition
			final Bundle extras = intent.getExtras();
			enterName.setText(extras.getString(ARG_DEFINITION_NAME));
			enterDescription.setText(extras.getString(ARG_DEFINITION_DESCRIPTION));
			frequencyTypeSpinner.setSelection(frequencyTypeAdapter.getPosition(extras.getString(ARG_DEFINITION_FREQUENCY_TYPE)));
			taskWeightSpinner.setSelection(weightAdapter.getPosition(extras.getString(ARG_DEFINITION_WEIGHT)));
			enterNumber.setText(String.valueOf(extras.getInt(ARG_DEFINITION_FREQUENCY)));

			definitionId = extras.getLong(ARG_DEFINITION_ID);

			nextButton.setVisibility(View.GONE);
			doneButton.setVisibility(View.GONE);
			saveButton.setVisibility(View.VISIBLE);
		}
	}

	private boolean needsRescheduling() {
		final Bundle extras = getIntent().getExtras();
		if (extras == null)
		{
			return false;
		}
		final boolean frequencyChanged = extras.getInt(ARG_DEFINITION_FREQUENCY) != Integer.parseInt(enterNumber.getText().toString());
		final boolean frequencyTypeChanged = !extras.getString(ARG_DEFINITION_FREQUENCY_TYPE).equals(String.valueOf(frequencyTypeSpinner.getSelectedItem()));
		final boolean weightChanged = !extras.getString(ARG_DEFINITION_WEIGHT).equals(taskWeightSpinner.getSelectedItem());

		return frequencyChanged || frequencyTypeChanged || weightChanged;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateToParent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void navigateToParent() {
		final Intent intent = NavUtils.getParentActivityIntent(this);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
			| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		NavUtils.navigateUpTo(this, intent);
	}

	private void submit() {

		enterName.setError(null);
		enterDescription.setError(null);
		enterNumber.setError(null);

		final String name = enterName.getText().toString();
		final String description = enterDescription.getText().toString();
		final String taskWeight = String.valueOf(taskWeightSpinner.getSelectedItem());
		final int frequency = Integer.parseInt(enterNumber.getText().toString());
		final String periodType = String.valueOf(frequencyTypeSpinner.getSelectedItem());

		final TaskRepetitionType taskRepType = TaskRepetitionType.valueOf(periodType);
		final TaskWeight tWeight = TaskWeight.valueOf(taskWeight);

		if (validateInputShowError(name, description, frequency)) {
			Log.e(TAG, "There was an error..?");
			return;
		}
		final TaskDefinitionDto definitionDto = TaskDefinitionDto.builder()
			.definitionId(definitionId)
			.groupId(groupId)
			.name(name)
			.description(description)
			.periodType(taskRepType)
			.weight(tWeight)
			.frequency(frequency)
			.build();

		taskService.createTask(definitionDto).enqueue(new Callback<TaskDefinitionDto>() {
			@Override
			public void onResponse(@NonNull final Call<TaskDefinitionDto> call, @NonNull final Response<TaskDefinitionDto> response) {
				if (!response.isSuccessful()) {
					final int responseCode = response.code();
					Log.e(TAG, String.format(getString(R.string.error_unknown_request_error), responseCode, response.message()));
				} else {
					Toast.makeText(TaskDefinitionActivity.this, "Task definition saved", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(@NonNull final Call<TaskDefinitionDto> call, @NonNull final Throwable t) {
				Log.e(TAG, "Calling backend failed! OMG!");
				t.printStackTrace();
			}
		});

	}

	private void reschedule()
	{
		groupService.rescheduleGroup(groupId, new GenerateScheduleDto(365)).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
				if (!response.isSuccessful()) {
					Toast.makeText(TaskDefinitionActivity.this, "Schedule has not been updated!", Toast.LENGTH_LONG).show();
				}
				Toast.makeText(TaskDefinitionActivity.this, "Schedule has been updated", Toast.LENGTH_LONG).show();
				TaskDefinitionActivity.this.navigateToParent();
			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
				Toast.makeText(TaskDefinitionActivity.this, "Unable to connect to server", Toast.LENGTH_LONG).show();
			}
		});
	}

	private boolean validateInputShowError(final String name, final String description,
	                                       final int frequency) {
		return (checkEmpty(name, enterName) || checkEmpty(description, enterDescription)
			|| checkEmpty(Integer.toString(frequency), enterNumber));
		// TODO check if more validation required
	}

	private boolean checkEmpty(final String text, final TextInputEditText editText) {
		if (TextUtils.isEmpty(text)) {
			editText.setError(getString(R.string.input_not_valid));
			return true;
		}
		return false;
	}
}

