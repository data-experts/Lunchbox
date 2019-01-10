package de.data_experts.lunchbox.android.client.adapter;

import de.data_experts.lunchbox.android.client.R;
import de.data_experts.lunchbox.android.client.model.Essen;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Adapter f�r eine Fehlerausschrift in der ExpendableListView
 * 
 * @author mbehrens
 * 
 */
public class FehlerAdapter extends BaseExpandableListAdapter {

	private String text;
	private Context context;

	public FehlerAdapter(Context context, String text) {
		this.context = context;
		this.text = text;
	}

	@Override
	public Essen getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 0;
	}

	@Override
	public String getGroup(int groupPosition) {
		return text;
	}

	@Override
	public int getGroupCount() {
		return 1;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View result = convertView;
		if (result == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			result = inflater.inflate(R.layout.main_fehler, parent);
		}
		TextView textViewAnbieter = result.findViewById(R.id.textViewFehler);
		textViewAnbieter.setText(text);
		return result;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
