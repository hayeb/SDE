package giphouse.nl.proprapp.ui.task;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.dagger.ImageService;
import giphouse.nl.proprapp.service.task.TaskService;
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

	@Inject
	ImageService imageService;

	private long taskId;
	private String completionDescription;
	private boolean isAssignee;

	private ImageView taskImage;

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

		if (isAssignee) {
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.task_complete_fragment, ViewTaskRatingsFragment.newInstance(taskId), "viewTaskRatingFragment")
				.disallowAddToBackStack()
				.commit();
		} else {
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.task_complete_fragment, RateTaskFragment.newInstance(taskId), "rateTaskFragment")
				.disallowAddToBackStack()
				.commit();
		}
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

		imageService.loadTaskImage(taskId)
			.placeholder(R.drawable.placeholder)
			.into(taskImage);
	}
}
