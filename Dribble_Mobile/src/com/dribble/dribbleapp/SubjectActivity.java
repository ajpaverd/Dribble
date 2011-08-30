// Authors: Dribble
// Date: 24 April 2010
// Class: TagActivty

package com.dribble.dribbleapp;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.dribble.common.DribSubject;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class SubjectActivity extends ListActivity {

	private static final String TAG = "SubjectActivity";

	// Store static selected subject and name
	public static DribSubject CurrentDribSubject = null;
	private Location myLoc;

	private ProgressDialog pd;
	private Handler mHandler = new Handler();
	public static ArrayList<DribSubject> dribTopAr;

	//Declare the telephony manager to get users IMEI
	private TelephonyManager telephonyManager;
	private String imei;

	//To change from static to non-static
	private GpsListener gpsListener;
	private DribCom dribCom;

	//For receiving geographic measurements
	public GeographicMeasurementsReceiver geographicMeasurementsReceiver;
	public Bundle geographicMeasurementsBundle;
		
	
	private Context context;
	private double primitiveLatitude = 22.00;
	private double primitiveLongitude = -28.0754;
	
	//To create a drill-down view
	private Intent dribsListIntent;


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subjects);
		Log.i(TAG, "Tab Loaded");		  

		//Create Location Object
		// Get current location TODO (if Network provider)
		String provider = LocationManager.GPS_PROVIDER;
		myLoc = new Location(provider);
		//Register broadcast receiver
		geographicMeasurementsReceiver = new GeographicMeasurementsReceiver(myLoc);
		this.registerReceiver(geographicMeasurementsReceiver, 
				new IntentFilter(Splash.BROADCAST_GEOGRAPHIC_MEASUREMENTS));
		
		//Create a new communication object
		dribCom = new DribCom(this);

		//Get IMEI Measurements for Like/Dislike
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		imei =  telephonyManager.getDeviceId();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.subject_menu, menu);
		
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
		case R.id.refresh_option_item:
			refreshContent();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void refreshContent()
	{
		// show progress dialog
		pd = new ProgressDialog(this);
		pd.setMessage("Retrieving Topics...");
		pd.setIndeterminate(true);
		pd.setCancelable(true);
		pd.show();

		Log.i(TAG,"Getting current Location");
		
		
		Log.i(TAG,"Latitude "+myLoc.getLatitude());
		Log.i(TAG,"Longitude "+myLoc.getLongitude());
		
		//Log.i(TAG,"Current Location is "+myLoc.getLatitude());
		final int results = DribbleSharedPrefs.getNumDribTopics(this);

		// Create thread to fetch subjects
		//
		Thread getDribSubjects = new Thread()
		{
			public void run()
			{
				Log.i(TAG,"Current Network Data Type is: "+telephonyManager.getNetworkType());
				if(telephonyManager.getNetworkType()!=0)
				{
					try{
						Log.i(TAG,"Retrieving Topics");
						//To prevent calling data again
						if(dribTopAr == null){
						
						dribTopAr = dribCom.getTopics(results);
						
						}


						if (dribTopAr != null && dribTopAr.size() != 0)
						{				
							Log.i(TAG, "Received List of Topics");
							mHandler.post(mUpdateResults);

							mHandler.post(mAddOverlays);
						}
						else
						{
							//If no topics, displays default "no topics available" message
							pd.dismiss();
							dribTopAr = new ArrayList<DribSubject>();
							CurrentDribSubject = null;
							mHandler.post(mUpdateResults);
						}

					}catch(NullPointerException npe)
					{
						Log.e(TAG, "The message is: "+npe.getMessage());
						//If no topics, displays default "no topics available" message
						pd.dismiss();
					}
				}
			}
		};

		// start Thread
		getDribSubjects.start();
	}

	// Create thread for adding all topics to map
	final Runnable mAddOverlays = new Runnable() {
		public void run()
		{
			OverlayItem overlayitem;
			if (MapsActivity.Itemizedoverlay != null)
			{
				MapsActivity.Itemizedoverlay.clearOverlays();
				for(DribSubject subject : dribTopAr)
				{			
					GeoPoint point = new GeoPoint((int)(subject.getLatitude() * 1E6),(int)(subject.getLongitude() * 1E6));
					overlayitem = new OverlayItem(point, "Drib Topic", subject.getName());
					MapsActivity.Itemizedoverlay.addOverlay(overlayitem);
				}
			}
		}
	};

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() 
	{
		public void run() 
		{
			updateResultsInUi();
		}
	};

	private void updateResultsInUi()
	{
		// Set list view
		setListAdapter(new SubjectAdapter(getApplicationContext(), R.layout.subject_row, dribTopAr));

		// dismiss dialog
		pd.dismiss();

		Log.i(TAG, "List Adapter Set");
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{		    	
				Log.i(TAG, "List Item Clicked");
				DribSubject selectedTopic = ((DribSubject)(dribTopAr.toArray())[position]);

				CurrentDribSubject = selectedTopic;
				
				dribsListIntent = new Intent(SubjectActivity.this, DribActivity.class);
				startActivity(dribsListIntent);
				//TabActivity tabActivity = (TabActivity)getParent();
				//TabHost tabHost = tabActivity.getTabHost();
				//tabHost.setCurrentTab(1);
			}
		});
	}
	@Override
	public void onResume() 
	{
		super.onResume();
		Log.i(TAG,"Resume was called");
		refreshContent();
	}

	// Class to hold custom list view information
	//
	private static class ViewHolder 
	{
		TextView message;
		TextView info;
	}

	// Show elapsed time since post
	private String getElapsed(long millis) 
	{
		long time = millis / 1000;
		//String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		int tempHours = (int) (time / 3600);
		String days = Integer.toString(tempHours / 24);
		String hours = Integer.toString(tempHours%24);

		minutes += "min ";
		if (hours.equals("0")) {
			hours = "";
		} else {
			hours += "hrs ";
		}
		if (days.equals("0")) {
			days = "";
		} else {
			days += "days ";
		}

		return days + hours + minutes;
	}

	// Custom list view implementation
	//
	private class SubjectAdapter extends ArrayAdapter<DribSubject> {

		private ArrayList<DribSubject> items;

		// Set list items
		public SubjectAdapter(Context context, int textViewResourceId, ArrayList<DribSubject> items)
		{
			super(context, textViewResourceId, items);
			this.items = items;
		}

		// Override default list view
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			final ViewHolder holder;
			if (convertView == null) 
			{
				LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.subject_row, null);

				holder = new ViewHolder();
				holder.message = (TextView) convertView.findViewById(R.id.subjectText);
				holder.info = (TextView) convertView.findViewById(R.id.subjectInfo);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}

			final DribSubject subject = items.get(position);

			if (subject != null)
			{
				holder.message.setText(subject.getName());

				Location subjectLoc = new Location("Subject Location");
				subjectLoc.setLatitude(subject.getLatitude());
				subjectLoc.setLongitude(subject.getLongitude());

				// Get distance in km
				double distance = myLoc.distanceTo(subjectLoc)/1000;
				DecimalFormat df = new DecimalFormat("#.##"); 

				// Set info text
				holder.info.setText(df.format(distance) + " km " + "("+ getElapsed(System.currentTimeMillis() - subject.getTime()) + "ago)" ); // + "\nDRank: " + drib.getPopularity());
			}
			return convertView; // return custom view
		}
	}


	@Override
	public void onPause(){
		super.onPause();
		
		pd.dismiss();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		pd.dismiss();
	}

	

	//	   @Override
	//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	//		   checkLocationProviders();
	//	super.onActivityResult(requestCode, resultCode, data);
	//	}
}