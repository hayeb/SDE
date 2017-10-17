package giphouse.nl.proprapp.service.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import giphouse.nl.proprapp.R;

/**
 * @author haye
 */
public class GroupListAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;

	private List<GroupDto> groupDtos = new ArrayList<>();

	public GroupListAdapter(final LayoutInflater layoutInflater)
	{
		this.mLayoutInflater = layoutInflater;
	}

	@Override
	public int getCount() {
		return groupDtos.size();
	}

	@Override
	public Object getItem(final int position) {
		return groupDtos.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final RelativeLayout itemView;
		if (convertView == null) {
			itemView = (RelativeLayout) mLayoutInflater.inflate(
					R.layout.group_list_item, parent, false);

		} else {
			itemView = (RelativeLayout) convertView;
		}

		final TextView titleText = itemView.findViewById(R.id.listTitle);
		final TextView descriptionText = itemView.findViewById(R.id.listDescription);

		final String title = groupDtos.get(position).getGroupName();
		titleText.setText(title);

		final String description = "Leden: " + groupDtos.get(position).getUsernames().stream().collect(Collectors.joining(", "));
		descriptionText.setText(description);

		return itemView;
	}

	public void updateEntries(final List<GroupDto> dtos)
	{
		this.groupDtos = dtos;
		notifyDataSetChanged();
	}
}
