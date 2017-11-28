package giphouse.nl.proprapp.ui.task;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskImagePayload;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;

/**
 * @author haye
 */
public class ShowCompletedTaskActivity extends AppCompatActivity {

	public static String ARG_TASK_ID = "taskId";

	public static String ARG_TASK_COMPLETION_NOTES = "completionNotes";

	@Inject
	TaskService taskService;

	private long taskId;
	private String completionDescription;

	TextView descriptionView;
	ImageView taskImage;
	TextView noImageMessageView;


	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			taskId = getIntent().getLongExtra(ARG_TASK_ID, 0);
			completionDescription = extras.getString(ARG_TASK_COMPLETION_NOTES);
		} else if (savedInstanceState != null) {
			taskId = savedInstanceState.getLong(ARG_TASK_ID);
			completionDescription = savedInstanceState.getString(ARG_TASK_COMPLETION_NOTES);
		}

		setContentView(R.layout.activity_show_completed_task);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle("View task");
		}


		descriptionView = findViewById(R.id.completed_task_notes);
		taskImage = findViewById(R.id.completed_task_image);
		noImageMessageView = findViewById(R.id.no_image_available);

		descriptionView.setText(completionDescription);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			final Intent intent = NavUtils.getParentActivityIntent(this);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		taskService.getTaskImage(taskId).enqueue(new Callback<TaskImagePayload>() {
			@Override
			public void onResponse(@NonNull final Call<TaskImagePayload> call, @NonNull final Response<TaskImagePayload> response) {
				if(response.isSuccessful()) {
					final byte[] image = response.body().getImage();
					if (image != null){
						taskImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
					} else {
						taskImage.setVisibility(INVISIBLE);
						noImageMessageView.setVisibility(View.VISIBLE);
					}
				}
				else {
					taskImage.setVisibility(INVISIBLE);
					noImageMessageView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFailure(@NonNull final Call<TaskImagePayload> call, @NonNull final Throwable t) {
				Log.e("tag", "there was a failure");
				t.printStackTrace();
			}
		});
	}
}
