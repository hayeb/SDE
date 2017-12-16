package giphouse.nl.proprapp.ui.group;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.GroupService;
import giphouse.nl.proprapp.ui.group.overview.GroupOverviewActivity;
import nl.giphouse.propr.dto.group.GroupDto;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";

    @Inject
    TaskService taskService;

    private Spinner spinner;
    private Spinner spinnerWeight;
    private TextInputEditText enterName;
    private TextInputEditText enterDescription;
    private TextInputEditText enterNumber;
    private Button doneButton;
    private Button nextButton;
    private long groupId;
    private String groupName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ProprApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_add_task);

        if (savedInstanceState != null) {
            groupName = savedInstanceState.getString("groupName");
            groupId = savedInstanceState.getLong("groupId");
        } else if (getIntent() != null && getIntent().getExtras() != null) {
            groupName = getIntent().getExtras().getString("groupName");
            groupId = getIntent().getExtras().getLong("groupId");
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
        doneButton = findViewById(R.id.buttonDone);
        nextButton = findViewById(R.id.buttonNext);

        spinner = findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter
            .createFromResource(this, R.array.frequencytypes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinnerWeight = findViewById(R.id.spinnerWeight);
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
            .createFromResource(this, R.array.weights,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeight.setAdapter(adapter2);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //submit();
                final RescheduleDialog dialog = RescheduleDialog.newInstance(groupId);
                dialog.show(getSupportFragmentManager(), "reschedule");
//                final Intent intent = new Intent(v.getContext(), GroupOverviewActivity.class);
//                intent.putExtra("groupId", groupId);
//                intent.putExtra("groupName", groupName);
//                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                submit();
                final Intent intent = new Intent(v.getContext(), AddTaskActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });
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
        return super.onOptionsItemSelected(item);
    }

    private void submit() {

        enterName.setError(null);
        enterDescription.setError(null);
        enterNumber.setError(null);

        final String name = enterName.getText().toString();
        final String description = enterDescription.getText().toString();
        final String taskWeight = String.valueOf(spinnerWeight.getSelectedItem());
        final int frequency = Integer.parseInt(enterNumber.getText().toString());
        final String periodType = String.valueOf(spinner.getSelectedItem());

        final TaskRepetitionType taskRepType = TaskRepetitionType.valueOf(periodType);
        final TaskWeight tWeight = TaskWeight.valueOf(taskWeight);

        if (validateInputShowError(name, description, frequency)) {
            Log.e(TAG, "There was an error..?");
            return;
        }

       taskService.createTask(new TaskDefinitionDto(groupId, name, description, taskRepType, tWeight, frequency)).enqueue(new Callback<TaskDefinitionDto>() {
           @Override
           public void onResponse(@NonNull final Call<TaskDefinitionDto> call, @NonNull final Response<TaskDefinitionDto> response) {
               if(!response.isSuccessful()) {
                   final int responseCode = response.code();
                   if (responseCode == 422) {
                            // needed???
                   } else {
                       Log.e(TAG, String.format(getString(R.string.error_unknown_request_error), responseCode, response.message()));
                   }
               } else {
                   Log.i(TAG, "Succesfully created a taskdefinition");
                   final TaskDefinitionDto dto = response.body();
               }
           }

           @Override
           public void onFailure(@NonNull final Call<TaskDefinitionDto> call, @NonNull final Throwable t) {
               Log.e(TAG, "Calling backend failed! OMG!");
               t.printStackTrace();
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

