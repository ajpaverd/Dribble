package com.dribble.dribbleapp;

import com.dribble.dribbleapp.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DribblePreferencesActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(
				DribbleSharedPrefs.PREFS_NAME);
		addPreferencesFromResource(R.xml.prefs);
	}
}