package giphouse.nl.proprapp.ui.task;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskImagePayload;
import nl.giphouse.propr.dto.task.TaskRatingDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;

/**
 * @author haye
 */
public class ShowCompletedTaskActivity extends AppCompatActivity {

	public static final String ARG_TASK_ID = "taskId";

	public static final String ARG_TASK_COMPLETION_NOTES = "completionNotes";

	public static final String ARG_IS_ASSIGNEE = "isAssignee";

	private static final String TAG = "CompletedTaskActivity";

	@Inject
	TaskService taskService;

	private long taskId;
	private String completionDescription;
	private boolean isAssignee;

	private ImageView taskImage;
	private TextView noImageMessageView;
	private RatingBar ratingBar;
	private TextInputEditText ratingText;

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getApplication()).getComponent().inject(this);

		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			taskId = extras.getLong(ARG_TASK_ID);
			completionDescription = extras.getString(ARG_TASK_COMPLETION_NOTES);
			isAssignee = extras.getBoolean(ARG_IS_ASSIGNEE);
		} else if (savedInstanceState != null) {
			taskId = savedInstanceState.getLong(ARG_TASK_ID);
			completionDescription = savedInstanceState.getString(ARG_TASK_COMPLETION_NOTES);
			isAssignee = savedInstanceState.getBoolean(ARG_IS_ASSIGNEE);
		}

		setContentView(R.layout.activity_show_completed_task);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		final ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle("View task");
		}

		final TextView descriptionView = findViewById(R.id.completed_task_notes);
		descriptionView.setText(completionDescription);

		taskImage = findViewById(R.id.completed_task_image);
		noImageMessageView = findViewById(R.id.no_image_available);
		ratingBar = findViewById(R.id.ratingBar);
		ratingText = findViewById(R.id.rating_comments);

		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(final RatingBar ratingBar, final float rating, final boolean fromUser) {
				if (rating < 0.5f) {
					ratingBar.setRating(0.5f);
				}
			}
		});

		// If the current user is the assignee of the task, we do not show the rating interface
		if (isAssignee) {
			ratingBar.setVisibility(INVISIBLE);
			ratingText.setVisibility(INVISIBLE);
			return;
		}

		findViewById(R.id.rating_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				submitRating();
			}
		});


		taskService.getTaskRating(taskId).enqueue(new Callback<TaskRatingDto>() {
			@Override
			public void onResponse(@NonNull final Call<TaskRatingDto> call, @NonNull final Response<TaskRatingDto> response) {
				if (response.isSuccessful()) {
					final TaskRatingDto dto = response.body();
					ratingBar.setRating(dto.getScore()/2.0f);
					ratingText.setText(dto.getComment());
				}
			}

			@Override
			public void onFailure(@NonNull final Call<TaskRatingDto> call, @NonNull final Throwable t) {

			}
		});
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

	private void submitRating() {
		final int score = (int) (ratingBar.getRating() * 2.0) + 1;
		final String comment = ratingText.getText().toString();

		taskService.rateTask(taskId, new TaskRatingDto(score, comment)).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
				if (response.isSuccessful()) {
					Toast.makeText(ShowCompletedTaskActivity.this, "Rating has been saved succesfully!", Toast.LENGTH_LONG).show();
				} else {
					Log.d(TAG, String.format("[%d]: %s", response.code(), response.message()));
					Toast.makeText(ShowCompletedTaskActivity.this, "Rating has not been saved", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
				Toast.makeText(ShowCompletedTaskActivity.this, "Unable to connect to server", Toast.LENGTH_LONG).show();
			}
		});
	}
}
