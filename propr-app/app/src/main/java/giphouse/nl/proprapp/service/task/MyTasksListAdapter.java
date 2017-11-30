package giphouse.nl.proprapp.service.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.ui.task.CompleteTaskActivity;
import nl.giphouse.propr.dto.task.TaskDto;

/**
 * @author haye
 */
public class MyTasksListAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;

	private List<TaskDto> taskDtos = new ArrayList<>();

	private final Context context;

	public MyTasksListAdapter(final LayoutInflater mLayoutInflater, final Context context) {
		this.mLayoutInflater = mLayoutInflater;
		this.context = context;
	}


	@Override
	public int getCount() {
		return taskDtos.size();
	}

	@Override
	public Object getItem(final int i) {
		return taskDtos.get(i);
	}

	@Override
	public long getItemId(final int i) {
		return i;
	}

	@Override
	public View getView(final int position, final View view, final ViewGroup parent) {
		final ConstraintLayout itemView;
		if (view == null) {
			itemView = (ConstraintLayout) mLayoutInflater.inflate(
				R.layout.item_task, parent, false);

		} else {
			itemView = (ConstraintLayout) view;
		}

		final TextView taskNameTextView = itemView.findViewById(R.id.task_name);
		final TextView descriptionTextView = itemView.findViewById(R.id.task_description);
		final TextView dueDateTextView = itemView.findViewById(R.id.task_due_date);
		final ImageButton taskCompleteButtun = itemView.findViewById(R.id.task_complete_button);

		final TaskDto dto = taskDtos.get(position);

		taskNameTextView.setText(dto.getName());
		descriptionTextView.setText(dto.getDescription());
		dueDateTextView.setText(dto.getDueDate());

		if (dto.isOverdue())
		{
			dueDateTextView.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
			dueDateTextView.setTypeface(null, Typeface.BOLD);
		}

		taskCompleteButtun.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Log.i("hue", "huehue");
				final Bundle bundle = new Bundle();
				bundle.putLong(CompleteTaskActivity.ARG_TASK_ID, dto.getTaskId());
				bundle.putString(CompleteTaskActivity.ARG_TASK_NAME, dto.getName());
				bundle.putString(CompleteTaskActivity.ARG_TASK_DESCRIPTION, dto.getDescription());
				final Intent intent = new Intent(context, CompleteTaskActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent, bundle);
			}
		});

		return itemView;
	}

	public void updateData(final List<TaskDto> tasks)
	{
		this.taskDtos = tasks;
		notifyDataSetChanged();
	}
}
