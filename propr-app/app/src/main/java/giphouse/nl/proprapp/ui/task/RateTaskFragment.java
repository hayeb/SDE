package giphouse.nl.proprapp.ui.task;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskRatingDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class RateTaskFragment extends Fragment {
	private static final String TAG = "RateTaskFragment";

	public static final String ARG_TASK_ID = "TaskId";

	@Inject
	TaskService taskService;

	private Long taskId;

	private RatingBar ratingBar;
	private TextInputEditText ratingText;
	private Button ratingButton;

	public static RateTaskFragment newInstance(final Long taskId)
	{
		final RateTaskFragment rateTaskFragment = new RateTaskFragment();
		final Bundle arguments = new Bundle();
		arguments.putLong(ARG_TASK_ID, taskId);
		rateTaskFragment.setArguments(arguments);
		return rateTaskFragment;
	}

	@Override
	public void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getActivity().getApplication()).getComponent().inject(this);

		final Bundle arguments = getArguments();
		taskId = arguments.getLong(ARG_TASK_ID);
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_rate_task, container, false);

		ratingBar = view.findViewById(R.id.ratingBar);
		ratingText = view.findViewById(R.id.rating_comments);
		ratingButton = view.findViewById(R.id.rating_button);

		ratingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				submitRating();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		taskService.getTaskRating(taskId).enqueue(new Callback<TaskRatingDto>() {
			@Override
			public void onResponse(@NonNull final Call<TaskRatingDto> call, @NonNull final Response<TaskRatingDto> response) {
				if (response.isSuccessful()) {
					final TaskRatingDto dto = response.body();
					if (dto != null)
					{
						ratingBar.setRating(dto.getScore());
						ratingText.setText(dto.getComment());
					}
				}
			}

			@Override
			public void onFailure(@NonNull final Call<TaskRatingDto> call, @NonNull final Throwable t) {

			}
		});
	}

	private void submitRating() {
		final int score = (int) ratingBar.getRating();
		final String comment = ratingText.getText().toString();

		if (score == 0)
		{
			Toast.makeText(this.getActivity(), "Please enter a rating", Toast.LENGTH_LONG).show();
			return;
		}

		final TaskRatingDto dto = TaskRatingDto.builder()
			.score(score)
			.comment(comment)
			.build();

		taskService.rateTask(taskId, dto).enqueue(new Callback<Void>() {
			@Override
			public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
				if (response.isSuccessful()) {
					Toast.makeText(RateTaskFragment.this.getActivity(), "Rating has been saved succesfully!", Toast.LENGTH_LONG).show();
				} else {
					Log.d(TAG, String.format("[%d]: %s", response.code(), response.message()));
					Toast.makeText(RateTaskFragment.this.getActivity(), "Rating has not been saved", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
				Toast.makeText(RateTaskFragment.this.getActivity(), "Unable to connect to server", Toast.LENGTH_LONG).show();
			}
		});
	}
}
