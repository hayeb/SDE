package giphouse.nl.proprapp.service.task;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.service.group.model.GroupListItemDto;
import giphouse.nl.proprapp.ui.group.overview.GroupTabbedActivity;

/**
 * @author haye
 */
public class TaskListAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;

	private List<UserTaskDto> taskDtos = new ArrayList<>();

	private final Context context;

	public TaskListAdapter(LayoutInflater mLayoutInflater, Context context) {
		this.mLayoutInflater = mLayoutInflater;
		this.context = context;
	}


	@Override
	public int getCount() {
		return taskDtos.size();
	}

	@Override
	public Object getItem(int i) {
		return taskDtos.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ConstraintLayout itemView;
		if (view == null) {
			itemView = (ConstraintLayout) mLayoutInflater.inflate(
				R.layout.item_task, parent, false);

		} else {
			itemView = (ConstraintLayout) view;
		}

		final TextView titleText = itemView.findViewById(R.id.task_name);

		final UserTaskDto dto = taskDtos.get(position);

		final String title = StringUtils.capitalize(dto.getName());
		titleText.setText(title);

		return itemView;
	}

	public void updateData(final List<UserTaskDto> tasks)
	{
		this.taskDtos = tasks;
		notifyDataSetChanged();
	}
}
