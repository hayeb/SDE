package giphouse.nl.proprapp.ui.task;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import giphouse.nl.proprapp.ProprApplication;
import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.dagger.ImageService;
import giphouse.nl.proprapp.service.task.TaskService;
import nl.giphouse.propr.dto.task.TaskRatingDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haye
 */
public class ViewTaskRatingsFragment extends ListFragment {
	public static final String ARG_TASK_ID = "taskId";
	private Long taskId;

	@Inject
	TaskService taskService;

	@Inject
	ImageService imageService;

	private RatingBar averageRatingBar;

	public static ViewTaskRatingsFragment newInstance(final Long taskId) {
		final ViewTaskRatingsFragment fragment = new ViewTaskRatingsFragment();
		final Bundle args = new Bundle();
		args.putLong(ARG_TASK_ID, taskId);
		fragment.setArguments(args);

		return fragment;
 	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((ProprApplication) getActivity().getApplication()).getComponent().inject(this);

		taskId = getArguments().getLong(ARG_TASK_ID);
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_view_task_rating, container, false);
		averageRatingBar = view.findViewById(R.id.average_rating_bar);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		taskService.getRatingsForTask(taskId).enqueue(new Callback<List<TaskRatingDto>>() {
			@Override
			public void onResponse(@NonNull final Call<List<TaskRatingDto>> call, @NonNull final Response<List<TaskRatingDto>> response) {
				if (response.isSuccessful())
				{
					ViewTaskRatingsFragment.this.setListAdapter(new TaskRatingsAdapter(response.body()));
				}
			}

			@Override
			public void onFailure(@NonNull final Call<List<TaskRatingDto>> call, @NonNull final Throwable t) {
				Toast.makeText(ViewTaskRatingsFragment.this.getActivity(), "Unable to connect to server", Toast.LENGTH_LONG).show();
			}
		});

		taskService.getAverageRatingForTask(taskId).enqueue(new Callback<Double>() {
			@Override
			public void onResponse(@NonNull final Call<Double> call, @NonNull final Response<Double> response) {
				if (response.isSuccessful())
				{
					final Double score = response.body();
					if (score != null && score > 0.0)
					{
						averageRatingBar.setVisibility(View.VISIBLE);
						averageRatingBar.setRating(score.floatValue());
					}
				}
			}

			@Override
			public void onFailure(@NonNull final Call<Double> call, @NonNull final Throwable t) {

			}
		});
	}

	private class TaskRatingsAdapter extends BaseAdapter {

		private final List<TaskRatingDto> ratings;

		TaskRatingsAdapter(final List<TaskRatingDto> ratings)
		{
			this.ratings = ratings;
		}

		@Override
		public int getCount() {
			return ratings.size();
		}

		@Override
		public Object getItem(final int position) {
			return ratings.get(position);
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final ConstraintLayout itemView;
			if (convertView == null) {
				itemView = (ConstraintLayout) LayoutInflater.from(ViewTaskRatingsFragment.this.getActivity()).inflate(
					R.layout.item_user_rating, parent, false);

			} else {
				itemView = (ConstraintLayout) convertView;
			}

			final TaskRatingDto dto = ratings.get(position);

			final ImageView image = itemView.findViewById(R.id.user_rating_image);
			final TextView comments = itemView.findViewById(R.id.comment);
			final TextView scoreView = itemView.findViewById(R.id.score_view);

			imageService.loadAccountAvatar(dto.getUserId()).placeholder(R.drawable.placeholder_avatar).into(image);
			comments.setText(dto.getComment());
			scoreView.setText(String.format(Locale.ENGLISH, "%d", dto.getScore()));

			return itemView;
		}
	}
}