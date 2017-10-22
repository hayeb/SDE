package giphouse.nl.proprapp.service.group.search;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import giphouse.nl.proprapp.R;

/**
 * @author haye
 */
public class GroupSearchAdapter extends BaseAdapter {

	private final LayoutInflater mLayoutInflater;
	private final Context mContext;

	private List<GroupSearchResult> results = new ArrayList<>();

	public GroupSearchAdapter(final LayoutInflater layoutInflater, final Context context) {
		this.mLayoutInflater = layoutInflater;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return results.size();
	}

	@Override
	public Object getItem(final int position) {
		return results.get(position);
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
				R.layout.search_result_group, parent, false);
		} else {
			itemView = (ConstraintLayout) convertView;
		}

		final TextView title = itemView.findViewById(R.id.groupSearchTitle);
		title.setText(results.get(position).getGroupName());

		return itemView;
	}

	public void updateEntries(final List<GroupSearchResult> newResults) {
		results = newResults;
		notifyDataSetChanged();
	}
}
