package giphouse.nl.proprapp.ui.group.schedule;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import giphouse.nl.proprapp.R;
import nl.giphouse.propr.dto.task.TaskDefinitionDto;
import nl.giphouse.propr.dto.task.TaskRepetitionType;
import nl.giphouse.propr.dto.task.TaskWeight;

/**
 * @author haye
 */
public class GroupScheduleAdapter extends BaseExpandableListAdapter {

	private final List<TaskRepetitionType> types;
	private final Map<TaskRepetitionType, List<TaskDefinitionDto>> data;
	private final GroupScheduleActivity activity;

	 GroupScheduleAdapter(final GroupScheduleActivity activity, final Map<TaskRepetitionType, List<TaskDefinitionDto>> data) {
	 	this.activity = activity;
	 	this.data = data;

	 	types = new ArrayList<>(data.keySet());
		 Collections.sort(types);
	 }

	@Override
	public int getGroupCount() {
		return types.size();
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		return data.get(types.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return data.get(types.get(groupPosition));
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		return data.get(types.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(R.layout.item_schedule_group, parent, false);
		}
		final String groupTitle = taskRepetitionName(types.get(groupPosition));

		final TextView groupHeader = view.findViewById(R.id.group_list_header);
		groupHeader.setText(groupTitle);

		return view;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(R.layout.item_schedule, parent, false);
		}
		final TaskDefinitionDto dto = (TaskDefinitionDto) getChild(groupPosition, childPosition);

		final TextView taskDefinitionName = view.findViewById(R.id.task_definition_name);
		taskDefinitionName.setText(dto.getName());

		final TextView taskDefinitionDescription = view.findViewById(R.id.task_definition_description);
		if (StringUtils.isEmpty(dto.getDescription()))
		{
			taskDefinitionDescription.setVisibility(View.GONE);
		} else {
			taskDefinitionDescription.setText(dto.getDescription());
		}

		final TextView weightView = view.findViewById(R.id.weight_text);
		weightView.setText(weightName(dto.getWeight()));

		final TextView frequencyView = view.findViewById(R.id.frequency_text);
		frequencyView.setText(String.format(Locale.ENGLISH, "%d", dto.getFrequency()));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				activity.editTask(dto);
			}
		});

		return view;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return true;
	}

	// TODO: Use resources
	private String taskRepetitionName(final @NonNull TaskRepetitionType type) {
		switch (type) {
			case DAY:
				return "Daily";
			case WEEK:
				return "Weekly";
			case MONTH:
				return "Monthly";
			case YEAR:
				return "Yearly";
			default:
				throw new IllegalArgumentException("Unknown value for TaskRepetitionType: " + type.name());

		}
	}

	// TODO: Use resources
	private String weightName(final @NonNull TaskWeight weight) {
	 	switch (weight) {

		    case LIGHT:
			    return "Short";
		    case MEDIUM:
			    return "Medium";
		    case HEAVY:
			    return "Long";
		    default:
		    	throw new IllegalArgumentException("Unknown value for TaskWeight:" + weight.name());
	    }
	}
}