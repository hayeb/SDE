package giphouse.nl.proprapp.ui.task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskCompletionDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class CompleteTaskActivity extends AppCompatActivity {

	public static String ARG_TASK_ID = "taskId";
	public static String ARG_TASK_NAME = "taskName";
	public static String ARG_TASK_DESCRIPTION = "taskDescription";

	@Inject
	TaskService taskService;

	private String taskName;
	private String taskDescription;
	private Long taskId;

	private TextInputEditText descriptionField;
	private ImageButton imageField;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		final Bundle arguments = getIntent().getExtras();
		if (arguments != null) {
			taskName = arguments.getString(ARG_TASK_NAME);
			taskDescription = arguments.getString(ARG_TASK_DESCRIPTION);
			taskId = arguments.getLong(ARG_TASK_ID);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		setContentView(R.layout.activity_task_complete);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("Complete \'" + taskName + "\'");

		final TextView descriptionText = findViewById(R.id.task_description);
		descriptionText.setText(taskDescription);

		descriptionField = findViewById(R.id.task_completion_notes);
		imageField = findViewById(R.id.task_complete_image);
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
		} else if (id == R.id.complete_task) {
			final Bitmap bitmap = ((BitmapDrawable) imageField.getDrawable()).getBitmap();
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			final byte[] byteArray = stream.toByteArray();
			taskService.completeTask(taskId, TaskCompletionDto.builder().taskCompletionDescription(descriptionField.getText().toString()).taskComppletionImage(byteArray).build()).enqueue(new Callback<Void>() {
				@Override
				public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
					final Intent intent = NavUtils.getParentActivityIntent(CompleteTaskActivity.this);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
					NavUtils.navigateUpTo(CompleteTaskActivity.this, intent);
					Toast.makeText(CompleteTaskActivity.this, "Task completed! Well done!", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {

				}
			});
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task_complete, menu);
		return true;
	}
}
