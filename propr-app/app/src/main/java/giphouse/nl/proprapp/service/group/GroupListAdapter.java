package giphouse.nl.proprapp.service.group;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import giphouse.nl.proprapp.R;
import giphouse.nl.proprapp.dagger.PicassoWrapper;
import giphouse.nl.proprapp.ui.group.overview.GroupOverviewActivity;
import nl.giphouse.propr.dto.group.GroupDto;

/**
 * @author haye
 */
public class GroupListAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;

	private List<GroupDto> groupListItemDtos = new ArrayList<>();

	private final Context context;

	private final PicassoWrapper picassoWrapper;

	public GroupListAdapter(final LayoutInflater layoutInflater, final Context context, final PicassoWrapper picassoWrapper) {
		this.mLayoutInflater = layoutInflater;
		this.context = context;
		this.picassoWrapper = picassoWrapper;
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
		final ImageView groupAvatarView = itemView.findViewById(R.id.group_avatar_image);

		groupAvatarView.setImageResource(R.drawable.placeholder_group);

		final GroupDto dto = groupListItemDtos.get(position);

		final String title = StringUtils.capitalize(dto.getGroupName());
		titleText.setText(title);

		final StringBuilder sb = new StringBuilder(dto.getUsernames().get(0));
		for (final String userName : dto.getUsernames().subList(1, dto.getUsernames().size())) {
			sb.append(", ");
			sb.append(userName);
		}

		descriptionText.setText(sb.toString());

		final Intent intent = new Intent(context, GroupOverviewActivity.class);
		intent.putExtra(GroupOverviewActivity.ARG_GROUP_NAME, dto.getGroupName());
		intent.putExtra(GroupOverviewActivity.ARG_GROUP_ID, dto.getGroupId());

		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				context.startActivity(intent);
			}
		});

		picassoWrapper.loadGroupImage(dto.getGroupId(), groupAvatarView);

		return itemView;
	}

	void updateEntries(final List<GroupDto> dtos) {
		this.groupListItemDtos = dtos;
		notifyDataSetChanged();
	}
}
