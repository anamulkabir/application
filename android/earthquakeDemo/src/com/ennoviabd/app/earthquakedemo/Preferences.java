package com.ennoviabd.app.earthquakedemo;



import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class Preferences extends Activity {
	CheckBox autoUpdate;
	Spinner updateFreqSpinner;
	Spinner magnitudeSpinner;
	public static final String USER_PREFERENCE = "USER_PREFERENCES";
	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
	public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";
	int minimumMagnitude = 0;
	boolean autoUpdateval = false;
	int updateFreq = 0;
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quake_autoupdate);
		updateFreqSpinner =	(Spinner)findViewById(R.id.spinner_update_freq);
		magnitudeSpinner = (Spinner)findViewById(R.id.spinner_quake_mag);
		autoUpdate = (CheckBox)findViewById(R.id.checkbox_auto_update);
		populateSpinners();
		prefs = getSharedPreferences(USER_PREFERENCE, Activity.MODE_PRIVATE);
		updateUIFromPreferences();
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View view) {
		savePreferences();
		Preferences.this.setResult(RESULT_OK);
		finish();
		}
		});
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View view) {
		Preferences.this.setResult(RESULT_CANCELED);
		finish();
		}
		});
		
		
	}
	private void savePreferences() {
		int updateIndex = updateFreqSpinner.getSelectedItemPosition();
		int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
		boolean autoUpdateChecked = autoUpdate.isChecked();
		Editor editor = prefs.edit();
		editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
		editor.putInt(PREF_UPDATE_FREQ, updateIndex);
		editor.putInt(PREF_MIN_MAG, minMagIndex);
		editor.commit();
	}
	private void updateUIFromPreferences() {
		boolean autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
		int updateFreqIndex = prefs.getInt(PREF_UPDATE_FREQ, 2);
		int minMagIndex = prefs.getInt(PREF_MIN_MAG, 0);
		updateFreqSpinner.setSelection(updateFreqIndex);
		magnitudeSpinner.setSelection(minMagIndex);
		autoUpdate.setChecked(autoUpChecked);
		}
	private void populateSpinners() {
		// Populate the update frequency spinner
		ArrayAdapter<CharSequence> fAdapter;
		fAdapter = ArrayAdapter.createFromResource(this,R.array.update_freq_options,android.R.layout.simple_spinner_item);
		fAdapter.setDropDownViewResource(
		android.R.layout.simple_spinner_dropdown_item);
		updateFreqSpinner.setAdapter(fAdapter);
		// Populate the minimum magnitude spinner
		ArrayAdapter<CharSequence> mAdapter;
		mAdapter = ArrayAdapter.createFromResource(this,R.array.magnitude_options,android.R.layout.simple_spinner_item);
		mAdapter.setDropDownViewResource(
		android.R.layout.simple_spinner_dropdown_item);
		magnitudeSpinner.setAdapter(mAdapter);
	}
	private void updateFromPreferences() {
		SharedPreferences prefs =	getSharedPreferences(Preferences.USER_PREFERENCE,	Activity.MODE_PRIVATE);
		int minMagIndex = prefs.getInt(Preferences.PREF_MIN_MAG, 0);
		if (minMagIndex < 0)
			minMagIndex = 0;
			int freqIndex = prefs.getInt(Preferences.PREF_UPDATE_FREQ, 0);
		if (freqIndex < 0)
			freqIndex = 0;
		autoUpdateval = prefs.getBoolean(Preferences.PREF_AUTO_UPDATE, false);
			Resources r = getResources();
		// Get the option values from the arrays.
		int[] minMagValues = r.getIntArray(R.array.magnitude);
		int[] freqValues = r.getIntArray(R.array.update_freq_values);
		// Convert the values to ints.
		minimumMagnitude = minMagValues[minMagIndex];
		updateFreq = freqValues[freqIndex];
	}

}
