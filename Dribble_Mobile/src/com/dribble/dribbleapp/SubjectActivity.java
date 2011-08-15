// Authors: Dribble
// Date: 24 April 2010
// Class: TagActivty

package com.dribble.dribbleapp;

import java.util.ArrayList;

import com.dribble.common.DribSubject;

import com.dribble.dribbleapp.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

public class SubjectActivity extends ListActivity {

	private static final String TAG = "TagActivity";
	
	// Store static selected subject and name
	public static int SubjectID = -1; // Default subject ID
	public static String SubjectName = "No Messages";
	public static DribSubject CurrentDribSubject = null;
	
	private ProgressDialog pd;
	private Handler mHandler = new Handler();
	private ArrayList<DribSubject> dribTopAr;
	private ArrayList<String> topicNames = new ArrayList<String>();
	
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
				if (dribTopAr != null)
				{
					topicNames.clear();
					for(DribSubject subject : dribTopAr)
					{
						topicNames.add(subject.getName());
					}
				
					Log.i(TAG, "Received List of Topics");
					mHandler.post(mUpdateResults);
					
					//mHandler.post(mAddOverlays);	don't add all subjects to map anymore
				}
//				else
//				{
//					Displays default "no subjects" message
//				}
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
			MapsActivity.Itemizedoverlay.clearOverlays();
			for(DribSubject subject : dribTopAr)
			{			
				GeoPoint point = new GeoPoint((int)(subject.getLatitude()),(int)(subject.getLongitude()));
			    overlayitem = new OverlayItem(point, "Drib Topic", subject.getName());
			    MapsActivity.Itemizedoverlay.addOverlay(overlayitem);
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
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.subject_row, topicNames));
	  
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
	    	
	    	SubjectID = selectedTopic.getSubjectID();
	    	CurrentDribSubject = selectedTopic;
	    	SubjectName = selectedTopic.getName();
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
}