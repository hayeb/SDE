package giphouse.nl.proprapp.ui.group.overview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import giphouse.nl.proprapp.R;
import nl.giphouse.propr.dto.task.TaskDto;
import nl.giphouse.propr.dto.task.TaskStatus;

/**
 * @author haye
 */
public class GroupTasksAdapter extends RecyclerView.Adapter<GroupTasksAdapter.ViewHolder> {

	private List<TaskDto> mValues = new ArrayList<>();
	private final OnGroupTasksFragmentInteractionListener mListener;

	GroupTasksAdapter(final OnGroupTasksFragmentInteractionListener listener) {
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.fragment_groupactivity, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final TaskDto task = mValues.get(position);
		holder.mItem = task;
		// TODO: Set image
		// holder.assigneeImageView
		holder.statusImageView.setImageResource(getImageResourceForStatus(task.getStatus()));
		holder.taskTitleView.setText(task.getName());
		holder.dueDateView.setText(task.getDueDate());

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (null != mListener) {
					// Notify the active callbacks interface (the activity, if the
					// fragment is attached to one) that an item has been selected.
					mListener.onGroupActivityFragmentInteraction(holder.mItem);
				}
			}
		});
	}

	private int getImageResourceForStatus(final TaskStatus status) {
		switch (status) {
			case DONE:
				return R.drawable.ic_assignment_turned_in_black_24dp;
			case OVERDUE:
				return R.drawable.ic_assignment_late_black_24dp;
			default:
				return R.drawable.ic_task_todo;
		}
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;

		final ImageView statusImageView;
		final ImageView assigneeImageView;
		final TextView taskTitleView;
		final TextView dueDateView;

		TaskDto mItem;

		ViewHolder(final View view) {
			super(view);
			mView = view;
			statusImageView = view.findViewById(R.id.task_status_image);
			assigneeImageView = view.findViewById(R.id.task_assignee_image);
			taskTitleView = view.findViewById(R.id.task_name_text);
			dueDateView = view.findViewById(R.id.task_due_date_text);

		}
	}

	public void updateEntries(final List<TaskDto> tasks)
	{
		mValues = tasks;
		notifyDataSetChanged();
	}
}
