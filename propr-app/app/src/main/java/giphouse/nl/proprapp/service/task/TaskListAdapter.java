package giphouse.nl.proprapp.service.task;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import giphouse.nl.proprapp.R;
import nl.giphouse.propr.dto.task.TaskDto;

/**
 * @author haye
 */
public class TaskListAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;

	private List<TaskDto> taskDtos = new ArrayList<>();

	private final Context context;

	public TaskListAdapter(final LayoutInflater mLayoutInflater, final Context context) {
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

		final TextView titleText = itemView.findViewById(R.id.task_name);

		final TaskDto dto = taskDtos.get(position);

		final String title = StringUtils.capitalize(dto.getName());
		titleText.setText(title);

		return itemView;
	}

	public void updateData(final List<TaskDto> tasks)
	{
		this.taskDtos = tasks;
		notifyDataSetChanged();
	}
}