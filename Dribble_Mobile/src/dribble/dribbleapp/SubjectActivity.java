// Authors: Dribble
// Date: 24 April 2010
// Class: TagActivty

package dribble.dribbleapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import dribble.dribbleapp.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import dribble.common.Drib;
import dribble.common.DribSubject;

public class SubjectActivity extends ListActivity {

	private static final String TAG = "TagActivity";
	public static int SubjectID = -1; // Default subject ID
	public static String SubjectName = "No Messages";
	public static DribSubject CurrentDribSubject = null;
//	MapView mapView;
//	MyLocationOverlay myLocOverlay;
	DribCom com = new DribCom();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.subjects);
		  Log.i(TAG, "Tab Loaded");
//		  mapView = new MapView(this, "0usNXcFTYLvKUSt_4ERmhFbYl2UD8_iUrDjlBhw");
//		    myLocOverlay = new MyLocationOverlay(this, mapView);
	}
    
	public void refreshContent()
	{
		final ArrayList<DribSubject> dribTopAr= com.getTopics();
		
		Log.i(TAG, "Received List of Topics");
		final String [] topicNameAr = new String[dribTopAr.size()]; 
		Runnable addOverlays = new Runnable(){
			public void run()
			{
				OverlayItem overlayitem;
				MapsActivity.Itemizedoverlay.clearOverlays();
				for(int i = 0; i< dribTopAr.size(); i++)
				{
					DribSubject ds = ((DribSubject)(dribTopAr.toArray())[i]);
					topicNameAr[i] = ds.getName();
					GeoPoint point = new GeoPoint((int)(ds.getLatitude()),(int)(ds.getLongitude()));
				    overlayitem = new OverlayItem(point, "Drib Topic", ds.getName());
				    MapsActivity.Itemizedoverlay.addOverlay(overlayitem);
				}
			}
		};
		Thread thread =  new Thread(null, addOverlays, "OverlayThread");
	    thread.start();		
		  
		  setListAdapter(new ArrayAdapter<String>(this, R.layout.subject_row, topicNameAr));
		  Log.i(TAG, "List Adapter Set");
		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {		    	
		    	Log.i(TAG, "List Item Clicked");
		    	DribSubject selectedTopic = ((DribSubject)(dribTopAr.toArray())[position]);
		    		    	
              Log.i(TAG, "Start Dribble Activity");
		    	
		    	SubjectID = selectedTopic.getSubjectID();
		    	CurrentDribSubject = selectedTopic;
		    	SubjectName = selectedTopic.getName();
		    	TabActivity tabActivity = (TabActivity)getParent();
		    	TabHost tabHost = tabActivity.getTabHost();
		    	tabHost.setCurrentTab(2);
		    }
		  });
 	
 	}
	@Override
	public void onResume() {
	    super.onResume();
			refreshContent();
	}
}