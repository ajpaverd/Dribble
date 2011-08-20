// Authors: Dribble
// Date: 24 April 2010
// Class: TagActivty

package com.dribble.dribbleapp;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.dribble.common.Drib;
import com.dribble.common.DribSubject;

import com.dribble.dribbleapp.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class SubjectActivity extends ListActivity {

	private static final String TAG = "TagActivity";
	
	// Store static selected subject and name
	public static DribSubject CurrentDribSubject = null;
	private static Location myLoc;
	
	private ProgressDialog pd;
	private Handler mHandler = new Handler();
	private ArrayList<DribSubject> dribTopAr;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.subjects);
		  Log.i(TAG, "Tab Loaded");		  
	}
    
	private void refreshContent()
	{
		// show progress dialog
		pd = new ProgressDialog(this);
		pd.setMessage("Retrieving Topics...");
		pd.setIndeterminate(true);
		pd.setCancelable(true);
		pd.show();
		
		// Get current location
	    myLoc = GpsListener.getLocation();
		
		final int results = DribbleSharedPrefs.getNumDribTopics(this);

		// Create thread to fetch subjects
		//
		Thread getDribSubjects = new Thread()
		{
			public void run()
			{
				dribTopAr= DribCom.getTopics(results);
				// If returned topics
				//
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
					CurrentDribSubject = null;
					mHandler.post(mUpdateResults);
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
					GeoPoint point = new GeoPoint((int)(subject.getLatitude()),(int)(subject.getLongitude()));
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
	    	TabActivity tabActivity = (TabActivity)getParent();
	    	TabHost tabHost = tabActivity.getTabHost();
	    	tabHost.setCurrentTab(1);
	    }
	   });
}
	@Override
	public void onResume() 
	{
	    super.onResume();
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
}