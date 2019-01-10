package de.data_experts.lunchbox.android.client.adapter;

import de.data_experts.lunchbox.android.client.R;
import de.data_experts.lunchbox.android.client.model.Essen;
import de.data_experts.lunchbox.android.client.model.EssensAnbieter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Adapter für ein Essen
 * 
 * @author mrosenow
 */
public class EssenlistAdapter extends BaseExpandableListAdapter {
	private List<EssensAnbieter> werte = new ArrayList<>();
	private GregorianCalendar datum;
	private Context context;


	public EssenlistAdapter(Context context, GregorianCalendar datum) {
		this.context = context;
		this.datum = datum;
		// Vorauswahl der Stadt aus den Preferences holen
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String choosenLocation = sharedPreferences.getString("pref_location_choose", null);

		if (choosenLocation != null) {
			for (EssensAnbieter anbieter : EssensAnbieter.getAlleAnbieter())
				if (anbieter.getEssensangebote(datum) != null && anbieter.getEssensangebote(datum).size() > 0
						&& anbieter.getLocation().equalsIgnoreCase(choosenLocation))
					werte.add(anbieter);
			Collections.sort(werte);
		} else {
			for (EssensAnbieter anbieter : EssensAnbieter.getAlleAnbieter())
				if (anbieter.getEssensangebote(datum) != null && anbieter.getEssensangebote(datum).size() > 0)
					werte.add(anbieter);
			Collections.sort(werte);
		}
	}

	@Override
	public Essen getChild(int groupPosition, int childPosition) {
		return werte.get(groupPosition).getEssensangebote(datum).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return werte.get(groupPosition).getEssensangebote(datum).get(childPosition).getId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		View result = convertView;
		if (result == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			result = inflater.inflate(R.layout.main_essen, parent, false);
		}

		TextView textViewName = result.findViewById(R.id.textViewName);
		TextView textViewPreis = result.findViewById(R.id.textViewPreis);

		Essen essen = getChild(groupPosition, childPosition);
		textViewPreis.setText(essen.getPreis());
		textViewName.setText(essen.getName());

		return result;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<Essen> essensangebote = werte.get(groupPosition).getEssensangebote(datum);
		return essensangebote == null ? 0 : essensangebote.size();
	}

	@Override
	public EssensAnbieter getGroup(int groupPosition) {
		return werte.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return werte.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return getGroup(groupPosition).getId();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View result = convertView;
		if (result == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			result = inflater.inflate(R.layout.main_anbieter, parent, false);
		}

		TextView textViewAnbieter = result.findViewById(R.id.textViewAnbieter);
		EssensAnbieter anbieter = getGroup(groupPosition);
		textViewAnbieter.setText(anbieter.getName());
		return result;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
