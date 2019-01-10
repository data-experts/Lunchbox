package de.data_experts.lunchbox.android.client;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * 
 * @author aneid
 * @version 1.1
 * 
 *          Version 1.1 2016-03-05 Umstellung auf DrawerMenu
 *          Version 1.0 2016-02-13 Initiale Version der SettingsActivity mit
 *          Auswahl der Stadt
 * 
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// arrays.xml dynamisch anpassbar????
	}

	@Override
	protected void onStart() {
		super.onStart();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		SettingsFragment settingsFragment = new SettingsFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(android.R.id.content, settingsFragment);
		fragmentTransaction.commit();

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Android Home Button
			finish();
			return true;
		default:
			return onContextItemSelected(item);
		}
	}

}
