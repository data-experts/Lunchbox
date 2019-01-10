package de.data_experts.lunchbox.android.client.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

public class DrawerMenuItem {
	
	public String menuItem;
	public Drawable menuIcon;
	
	public DrawerMenuItem(@NonNull Activity activity, int stringId, int menuIconId) {
		this.menuItem = activity.getString( stringId );
		this.menuIcon = ContextCompat.getDrawable(activity, menuIconId);
	}
}