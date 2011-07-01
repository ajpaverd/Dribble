// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: DribbleTabs

package dribble.dribbleapp;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

// Configures and displays all tabs and menu options
public class DribbleTabs extends TabActivity 
{
	private static final String TAG = "DribbleTabs";
	
	/** Called when the activity is first created.
	 * Used to allow updates when screen orientation changes*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		Log.w(TAG, "Orientation Changed");
		super.onConfigurationChanged(newConfig);
	}

	// Creates menu using resource (could be seperated into another class?)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.drib_menu, menu);
		
		Intent prefsIntent = new Intent(this, DribblePreferencesActivity.class);
		MenuItem preferences = menu.findItem(R.id.settings_option_item);
		preferences.setIntent(prefsIntent);
		
		return true;
	}

	// 
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
			// help menu item selected
		   //
			case R.id.help:
				AlertDialog.Builder builderHelp = new AlertDialog.Builder(this);
				builderHelp.setMessage(R.string.help_text).setCancelable(true).setTitle("Help").setNeutralButton("OK", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog help = builderHelp.create();
			help.show();
			return true;
			
	    // About menu item selected
		//
		case R.id.about:
			AlertDialog.Builder builderAbout = new AlertDialog.Builder(this);
			builderAbout.setMessage(R.string.about_text).setCancelable(true).setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog about = builderAbout.create();
			about.show();
			return true;
		case R.id.settings_option_item:
			getCurrentActivity().startActivity(item.getIntent());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Tabs Loaded");
		setContentView(R.layout.tabs);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

//		// Create an Intent to launch an Activity for the tab (to be reused)
//		intent = new Intent().setClass(this, MapsActivity.class);
//		// Initialize a TabSpec for each tab and add it to the TabHost
//		spec = tabHost.newTabSpec("maps").setIndicator("Map",
//				res.getDrawable(R.drawable.ic_tab_map)).setContent(intent);
//		tabHost.addTab(spec);
//		Log.i(TAG, "Add Maps Tab");

		// Do the same for the other tabs
		intent = new Intent().setClass(this, SubjectActivity.class);
		spec = tabHost.newTabSpec("topics").setIndicator("Topics",
				res.getDrawable(R.drawable.ic_tab_topics)).setContent(intent);
		tabHost.addTab(spec);
		Log.i(TAG, "Add Tag Tab");

		intent = new Intent().setClass(this, DribActivity.class);
		spec = tabHost.newTabSpec("msg").setIndicator("Dribs",
				res.getDrawable(R.drawable.ic_tab_dribs)).setContent(intent);
		tabHost.addTab(spec);
		Log.i(TAG, "Add Message Tab");

		intent = new Intent().setClass(this, CreateDribActivity.class);
		spec = tabHost.newTabSpec("make_drib").setIndicator("New",
				res.getDrawable(R.drawable.ic_tab_new)).setContent(intent);
		tabHost.addTab(spec);
		Log.i(TAG, "Add Create Drib Tab");

		tabHost.setCurrentTab(0);
		Log.i(TAG, "Set Default Tab");
	}
}
