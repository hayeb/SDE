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
import giphouse.nl.proprapp.dagger.ImageService;
import nl.giphouse.propr.dto.task.TaskDto;

/**
 * @author haye
 */
public class GroupTasksAdapter extends RecyclerView.Adapter<GroupTasksAdapter.ViewHolder> {

	private List<TaskDto> mValues = new ArrayList<>();
	private final OnGroupTasksFragmentInteractionListener mListener;
	private ImageService mImageService;

	GroupTasksAdapter(final OnGroupTasksFragmentInteractionListener listener, final ImageService imageService) {
		this.mListener = listener;
		this.mImageService = imageService;
	}

	@Override
	public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.item_grouptask, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final TaskDto task = mValues.get(position);
		holder.mItem = task;
		holder.statusImageView.setImageResource(getImageResourceForStatus(task));
		holder.taskTitleView.setText(task.getName());
		holder.dueDateView.setText(task.getCompletionDate() != null ? task.getCompletionDate() : task.getDueDate());

		mImageService.loadAccountAvatar(task.getAssigneeId()).placeholder(R.drawable.placeholder_avatar).into(holder.assigneeImageView);

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (null != mListener && !task.isOverdue()) {
					mListener.onGroupActivityFragmentInteraction(holder.mItem);
				}
			}
		});
	}

	private int getImageResourceForStatus(final TaskDto dto) {
		if (dto.isOverdue()) {
			return R.drawable.ic_assignment_late_black_24dp;
		} else if (dto.getCompletionDate() != null) {
			return R.drawable.ic_assignment_turned_in_black_24dp;
		} else {
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

	public void updateEntries(final List<TaskDto> tasks) {
		mValues = tasks;
		notifyDataSetChanged();
	}
}
