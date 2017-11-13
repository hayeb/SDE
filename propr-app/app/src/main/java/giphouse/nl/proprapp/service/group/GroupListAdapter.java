package giphouse.nl.proprapp.service.group;

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
public class GroupListAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;

	private List<GroupListItemDto> groupListItemDtos = new ArrayList<>();

	private final Context context;

	public GroupListAdapter(final LayoutInflater layoutInflater, final Context context) {
		this.mLayoutInflater = layoutInflater;
		this.context = context;
	}

	@Override
	public int getCount() {
		return groupListItemDtos.size();
	}

	@Override
	public Object getItem(final int position) {
		return groupListItemDtos.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final ConstraintLayout itemView;
		if (convertView == null) {
			itemView = (ConstraintLayout) mLayoutInflater.inflate(
				R.layout.item_group, parent, false);

		} else {
			itemView = (ConstraintLayout) convertView;
		}

		final TextView titleText = itemView.findViewById(R.id.listTitle);
		final TextView descriptionText = itemView.findViewById(R.id.listDescription);

		final GroupListItemDto dto = groupListItemDtos.get(position);

		final String title = StringUtils.capitalize(dto.getGroupName());
		titleText.setText(title);

		final String description = dto.getUsernames().stream().collect(Collectors.joining(", "));
		descriptionText.setText(description);

		final Intent intent = new Intent(context, GroupTabbedActivity.class);
		intent.putExtra("group", dto);

		itemView.setOnClickListener(l -> context.startActivity(intent));

		return itemView;
	}

	void updateEntries(final List<GroupListItemDto> dtos) {
		this.groupListItemDtos = dtos;
		notifyDataSetChanged();
	}
}
