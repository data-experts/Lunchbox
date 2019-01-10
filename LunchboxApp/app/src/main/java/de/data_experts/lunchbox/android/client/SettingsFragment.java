package de.data_experts.lunchbox.android.client;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

/**
 * 
 * @author aneid
 * @version 1.0
 * 
 *          Version 1.0 2016-02-13 Initiale Version des SettingsFragment
 * 
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		addPreferencesFromResource(R.xml.settings);

	}

	@Override
	public void onResume() {
		super.onResume();
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			initPrefSummary(getPreferenceScreen().getPreference(i));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePrefSummary(findPreference(key));
	}

	/**
	 * �berpr�ft die �bergebene Preference, ob es eine PreferenceCategory ist,
	 * ermittelt im Positivfall die zugeh�rigen Preferences und ruft zur
	 * Aktualiserung updatePrefSummery auf
	 * 
	 * @param p ungleich null
	 */
	private void initPrefSummary(Preference p) {
		if (p instanceof PreferenceCategory) {
			PreferenceCategory pCat = (PreferenceCategory) p;
			for (int i = 0; i < pCat.getPreferenceCount(); i++) {
				initPrefSummary(pCat.getPreference(i));
			}
		} else {
			updatePrefSummary(p);
		}
	}

	/**
	 * Aktualisiert im Summary der Preference den aktuell eingestellten Wert
	 * (aktuell nur die eine ListPreference ;) )
	 * 
	 * @param p ungleich null
	 */
	private void updatePrefSummary(Preference p) {
		if (p instanceof ListPreference) {
			ListPreference listPref = (ListPreference) p;
			CharSequence actualSummary = listPref.getSummary();
			CharSequence actualSetting = listPref.getEntry();
			if (actualSetting == null) {
				actualSetting = getString( R.string.standort_nicht_gewaehlt );
			}
			int index = ((String) actualSummary).indexOf(" :");
			if (index > -1) {
				p.setSummary(((String) actualSummary).substring(0, index) + " : " + actualSetting);
			} else {
				p.setSummary(actualSummary + " : " + actualSetting);
			}
		}
	}

}
