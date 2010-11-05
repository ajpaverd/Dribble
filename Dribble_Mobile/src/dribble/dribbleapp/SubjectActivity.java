// Authors: Dribble
// Date: 24 April 2010
// Class: TagActivty

package dribble.dribbleapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import dribble.common.DribSubject;

public class SubjectActivity extends ListActivity {

	private static final String TAG = "TagActivity";
	public static int SubjectID = -1; // Default subject ID
	public static String SubjectName = "No Messages";
	public static DribSubject CurrentDribSubject = null;
	private ProgressDialog pd;
	private Handler mHandler = new Handler();
	private ArrayList<DribSubject> dribTopAr;
	private String [] topicNameAr;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.subjects);
		  Log.i(TAG, "Tab Loaded");		  
	}
    
	private void refreshContent()
	{
//		pd = new ProgressDialog(this);
//		pd.setMessage("Retrieving Subjects...");
//		pd.setIndeterminate(true);
//		pd.setCancelable(true);
//		pd.show();

		Thread getDribSubjects = new Thread() {
			public void run() {
				dribTopAr= DribCom.getTopics();
				topicNameAr = new String[dribTopAr.size()];
				for(int i = 0; i< dribTopAr.size(); i++)
				{
					DribSubject ds = ((DribSubject)(dribTopAr.toArray())[i]);
					topicNameAr[i] = ds.getName();
				}
				
				Log.i(TAG, "Received List of Topics");
				mHandler.post(mUpdateResults);
				//mHandler.post(mAddOverlays);								
			}
		};
		getDribSubjects.start();
	}
	
//	// Create runnable for posting
//	final Runnable mAddOverlays = new Runnable() {
//		public void run()
//		{
//			OverlayItem overlayitem;
//			MapsActivity.Itemizedoverlay.clearOverlays();
//			for(int i = 0; i< dribTopAr.size(); i++)			{
//				DribSubject ds = ((DribSubject)(dribTopAr.toArray())[i]);
//				GeoPoint point = new GeoPoint((int)(ds.getLatitude()),(int)(ds.getLongitude()));
//			    overlayitem = new OverlayItem(point, "Drib Topic", ds.getName());
//			    MapsActivity.Itemizedoverlay.addOverlay(overlayitem);
//			}
//		}
//	};
	
	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}
	};
	
	private void updateResultsInUi() {
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.subject_row, topicNameAr));
	  
	  //pd.dismiss();
	  
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
	    	tabHost.setCurrentTab(1);
	    }
	    	  });
}
	@Override
	public void onResume() {
	    super.onResume();
			refreshContent();
	}
}