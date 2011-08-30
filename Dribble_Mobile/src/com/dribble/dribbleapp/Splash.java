// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: Splash

package com.dribble.dribbleapp;

import java.util.Timer;

import com.dribble.dribbleapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

	
	public class Splash extends Activity {
		
		private static final String TAG = "Splash";
		// Splash screen display length (milliseconds)
	    private static final int SPLASH_DISPLAY_LENGTH = 1500;
	    
	  //Declare the parameters to check GPS Status
		static String provider;
		static LocationManager locationManager;

		//create the new geographic code using bundles
		private GpsListener gpsListener;
		public static final String BROADCAST_GEOGRAPHIC_MEASUREMENTS = "com.dribble.dribbleapp.gpsMeasurements";
		
		//Create Bundle
		private Bundle locationBundle;
		private Timer locationTimer;
		
		//Timer thread to refresh content
		private Timer refreshContentTimer;
		
	    
	     @Override
	     public void onCreate(Bundle icicle) 
	     {
	          super.onCreate(icicle);
	          Log.i(TAG, "Splash displayed");
	          setContentView(R.layout.splash);
	          
	        //Create the location manager
	  		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	  		//				locationManager.addGpsStatusListener(GpsListener.class);

	  		//check if location providers are available
	  		checkLocationProviders();

	  		//Initialise the GPS Bundle to be used -last known location
	  		//TODO Add last known location
	  		initialiseGpsBundle();
	  		
	  		// Start location listening
	  		gpsListener = new GpsListener(this, locationBundle);
	  		
	  		//Create a broadcast task to broadcast the measurements every 'x' seconds
	  		BroadcastGpsTask broadcastTask = new BroadcastGpsTask(this, locationBundle);
	  		locationTimer = new Timer();
	  		locationTimer.schedule(broadcastTask, 0, 5000);
	  		
	  		//Create a timer thread that refreshes the content
	  		RefreshTimerTask refreshTask = new RefreshTimerTask(this);
	  		refreshContentTimer = new Timer();
	  		refreshContentTimer.schedule(refreshTask, 0, 60000);
	  		
	          
	          /* New Handler to start the Menu-Activity
	           * and close this Splash-Screen after some seconds.*/
	          new Handler().postDelayed(new Runnable()
	          {
	               public void run() 
	               {
	                    /* Create an Intent that will start the Menu-Activity. */
	                    Intent mainIntent = new Intent(Splash.this,DashboardActivity.class);
	                    Log.i(TAG, "Start Dribble Activity");
	                    Splash.this.startActivity(mainIntent);
	                   
	                    Splash.this.finish();
	                    Log.i(TAG, "Splash Activity Finished");
	               }
	          }, SPLASH_DISPLAY_LENGTH);
	     }
	     
	     private void checkLocationProviders(){
	 		Log.i(TAG,"checking available Location providers");
	 		//String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	 		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

	 			Toast toast = Toast.makeText(Splash.this, "GPS provider Enabled: ",Toast.LENGTH_LONG);
	 			toast.show();

	 		}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

	 			Toast toast = Toast.makeText(Splash.this, "Network provider Enabled: ",Toast.LENGTH_LONG);
	 			toast.show();

	 		}
	 		else
	 		{
	 			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 			builder.setMessage("Location providers are not available. Enable GPS or network providers.")
	 			.setCancelable(false)
	 			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	 				public void onClick(DialogInterface dialog, int id) {
	 					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	 					startActivityForResult(intent, 1);
	 				}
	 			})
	 			.setNegativeButton("No", new DialogInterface.OnClickListener() {
	 				public void onClick(DialogInterface dialog, int id) {

	 					Toast toast = Toast.makeText(Splash.this, "Please note that you need location to use Dribble ",Toast.LENGTH_LONG);
	 					toast.show();
	 					Splash.this.finish();
	 				}
	 			});
	 			builder.show();
	 		}
	 	}



	 	@Override
	 	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 		checkLocationProviders();
	 		super.onActivityResult(requestCode, resultCode, data);
	 	}
	 	
	 	//Initialise the GpsBundle
	 	private void initialiseGpsBundle()
	 	{
	 		double latitude =22.00;
	 		double longitude = -28.87;
	 		locationBundle = new Bundle();
	 		//Initialise with the last known location. Still need to determine location 
	 		//is more accurate based on time
	 		try
	 		{
	 		Log.i(TAG,"Is GPS enabled? "+locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
	 		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	 		{
	 			latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
	 			longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
	 			Log.i(TAG, "GPS last known latitude "+latitude);
	 			Log.i(TAG, "GPS last known longitude "+longitude);
	 			locationBundle.putDouble("myLatitude", latitude);
	 			locationBundle.putDouble("myLongitude", longitude);
	 		}
	 		else
	 			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	 				
	 				latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
	 				longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
	 				Log.i(TAG, "Network last known latitude "+latitude);
	 				Log.i(TAG, "Network last known longitude "+longitude);
	 				
	 				locationBundle.putDouble("myLatitude", latitude);
	 				locationBundle.putDouble("myLongitude", longitude);
	 				
	 			}
	 		}catch(NullPointerException npe){
	 			Log.e(TAG,"Null pointer for initialising bundles");
	 		}
	 		
	 	}
	 	
	 	@Override
	 	public void onPause(){
	 		super.onPause();
	 	}
	 	@Override
	 	public void onResume(){
	 		super.onResume();
	 	}
	}

