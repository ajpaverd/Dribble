// Authors: Dribble
// Date: 24 April 2010
// Class: DribbleTabs

package dribble.dribbleapp;

import dribble.dribbleapp.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class DribbleTabs extends TabActivity  {
    /** Called when the activity is first created. */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.w(TAG, "Override Function - Change Configuration");
		super.onConfigurationChanged(newConfig);		
	}

	private static final String TAG = "DribbleTabs";
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	           
	    Log.i(TAG, "Tabs Loaded");
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    Log.i(TAG, "Load Drawable Items From Resources");
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, MapsActivity.class);
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("maps").setIndicator("Map",
	                      res.getDrawable(R.drawable.ic_tab_map))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    Log.i(TAG, "Add Maps Tab");

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, SubjectActivity.class);
	    spec = tabHost.newTabSpec("subjects").setIndicator("Subjects",
	                      res.getDrawable(R.drawable.ic_tab_topics))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    Log.i(TAG, "Add Tag Tab");

	    intent = new Intent().setClass(this, DribActivity.class);
	    spec = tabHost.newTabSpec("msg").setIndicator("Dribs",
	                      res.getDrawable(R.drawable.ic_tab_dribs))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    Log.i(TAG, "Add Message Tab");
	    
	    intent = new Intent().setClass(this, CreateDribActivity.class);
	    spec = tabHost.newTabSpec("make_drib").setIndicator("New",
	                      res.getDrawable(R.drawable.ic_tab_new))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    Log.i(TAG, "Add Create Drib Tab");

	    tabHost.setCurrentTab(0);
	    Log.i(TAG, "Set Default Tab");
	}
}

