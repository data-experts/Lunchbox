package de.data_experts.lunchbox.android.client.adapter;

import de.data_experts.lunchbox.android.client.R;
import de.data_experts.lunchbox.android.client.model.DrawerMenuItem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter für einen Eintrag im DrawerMenu
 * 
 * @author aneid
 * @version 1.0
 * 
 */
public class MenuItemAdapter extends ArrayAdapter<DrawerMenuItem> {

	private Context context;
	private int layout;
	private ArrayList<DrawerMenuItem> menuItems;

	public MenuItemAdapter(Context context, int layout, ArrayList<DrawerMenuItem> menuItems) {
		super(context, layout, menuItems);
		this.context = context;
		this.layout = layout;
		this.menuItems = menuItems;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		DrawerMenuItem menuItem = menuItems.get(position);
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layout, parent, false);
		}
		ImageView icon = convertView.findViewById(R.id.menuicon);
		TextView item = convertView.findViewById(R.id.menuitem);
		icon.setImageDrawable(menuItem.menuIcon);
		item.setText(menuItem.menuItem);
		return convertView;
	}
}