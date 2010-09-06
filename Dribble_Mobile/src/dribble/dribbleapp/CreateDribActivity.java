// Authors: Dribble
// Date: 24 April 2010
// Class: DribbleTabs

package dribble.dribbleapp;

import dribble.dribbleapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import dribble.common.Drib;
import dribble.common.DribSubject;

public class CreateDribActivity extends Activity//extends MapActivity implements LocationListener{
{
	
//	MapView mapView;
//	MyLocationOverlay myLocOverlay;
//	LocationManager locMgr = null;
//	int latitude;
//	int longitude;
	private static final String TAG = "CreateDribActivity";
	public static boolean newMessage = true;
	DribSubject dribSubject;
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Tab Loaded");
        setContentView(R.layout.inputdrib);
             
//        mapView = new MapView(this, "0usNXcFTYLvKUSt_4ERmhFbYl2UD8_iUrDjlBhw");
//        myLocOverlay = new MyLocationOverlay(this, mapView);
	}
	
	public void refreshContent()
	{
		final EditText et = (EditText)findViewById(R.id.topicInput);
		EditText dribTopic = (EditText)findViewById(R.id.topicInput);
		dribTopic.setText("");
		et.setText("");
		 if (newMessage==false)
     	{
     	dribSubject = SubjectActivity.CurrentDribSubject;
     	TextView tv = (TextView)findViewById(R.id.topicLabel);
     	tv.setText("Topic: " + dribSubject.getName());
     	tv = (TextView)findViewById(R.id.heading);
     	tv.setText("Reply");     	
//     	et.setText(dribSubject.getName());
     	et.setVisibility(EditText.INVISIBLE);
     	
     	}
		//locMgr = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
	        //locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
	        Button buttonSubmit = (Button)findViewById(R.id.submit);
	        buttonSubmit.setOnClickListener(new OnClickListener() 
	        {
			    public void onClick(View v)
			    {
			    	Log.i(TAG, "Button Listener Activated (Button Clicked)");
//			    	myLocOverlay.enableMyLocation();
//				
//					myLocOverlay.runOnFirstFix(new Runnable() {
//			            public void run() {
//			                int latitude = myLocOverlay.getMyLocation().getLatitudeE6();
//			                int longitude= myLocOverlay.getMyLocation().getLongitudeE6();
			    	//Close the application
			    	EditText dribTopic = (EditText)findViewById(R.id.topicInput);
			    	String dripTopicName = dribTopic.getText().toString();
			    	if (newMessage)
			    		dribSubject = new DribSubject (dripTopicName, 0, MapsActivity.LATITUDE, MapsActivity.LONGTIUDE, 0, 0, System.currentTimeMillis(), 0);			    		
			            
			    	EditText dribMessage = (EditText)findViewById(R.id.dribInput);
			    	String dribText = dribMessage.getText().toString();
			    	Drib newDrib = new Drib(dribSubject, dribText, MapsActivity.LATITUDE, MapsActivity.LONGTIUDE);
			    	DribCom dribCom = new DribCom();
			    	Log.i(TAG, "Submit new message");
			    	dribCom.sendDrib(newDrib);
			    	Log.i(TAG, "Message Successfully Submitted");
			    	AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
			  	  dialog.setTitle("Success!");
			  	  dialog.setMessage("Message sent successfully");
			  	  dialog.show();
			  	TabActivity tabActivity = (TabActivity)getParent();
		    	TabHost tabHost = tabActivity.getTabHost();
		    	if (newMessage == true)
		    	{
		    		tabHost.setCurrentTab(1);
		    	}
		    	else
		    	{
			    	tabHost.setCurrentTab(2);
			    }
			  	  dribMessage.setText("");
			  	  et.setText("");
			    	    }
			        });
					    }		

	
    
	@Override
    public void onResume() {
        super.onResume();
    		refreshContent();
    }
	
	@Override
	public void onPause() {
		super.onPause();

		newMessage=true;
		TextView tv = (TextView)findViewById(R.id.heading);
     	tv.setText("Create New");
     	tv = (TextView)findViewById(R.id.topicLabel);
     	tv.setText("Topic");
     	EditText et = (EditText)findViewById(R.id.topicInput);
     	et.setVisibility(EditText.VISIBLE);
	}
}
//	
//    @Override
//    protected boolean isRouteDisplayed() {
//   
//        return false;
//    } 
//	
//    public void onLocationChanged(Location location) {
//        this.latitude = (int)location.getLatitude();
//        this.longitude = (int)location.getLongitude();
//        //Log.i("Location change",""+latitude+ " : "+longitude) ;
//    }
//    
//    public void onProviderDisabled(String provider) {
//       
//    }
//    
//    public void onProviderEnabled(String provider) {
//       
//    }
//    
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//       
//    }
//}