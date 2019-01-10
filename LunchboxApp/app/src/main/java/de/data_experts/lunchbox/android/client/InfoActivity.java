package de.data_experts.lunchbox.android.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * @author aneid
 */
public class InfoActivity extends Activity {
	
	protected final static String TAG = InfoActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (getActionBar() != null) {
		  getActionBar().setDisplayHomeAsUpEnabled(true);
		  getActionBar().setHomeButtonEnabled(true);
		}
		WebView wv = findViewById(R.id.html);
		wv.loadData(readRawTextFile(R.raw.info, InfoActivity.this), "text/html", null);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i(TAG, String.valueOf(item.getItemId()));
		switch(item.getItemId()) {
			case android.R.id.home:
				// Android Home Button
				finish();
				return(true);
			default:
				return onContextItemSelected(item);
		}
	}
	
	/**
	 * File über Ressourcen-ID einlesen und als String zurückgeben
	 * 
	 * @param id der Ressource (R.id.*)
	 * @param context der aufrufenden Klasse/Activity
	 * @return String mit der eingelesenen Textdatei
	 */
	@Nullable
	public static String readRawTextFile(int id,@NonNull Context context) {
		InputStream inputStream = context.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in, 8);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while (( line = buf.readLine()) != null) {
				text.append(line);
			}
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}

}
