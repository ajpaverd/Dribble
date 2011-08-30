package com.dribble.dribbleapp;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class DashboardActivity extends Activity{

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

	//check gprs
	boolean dataConnection  = false;
	boolean locationConnection = false;
	
	public static final String TAG = "DashboardActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		//Create the location manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		//				locationManager.addGpsStatusListener(GpsListener.class);

		//check if location providers are available
		checkLocationProviders();

		//check if the application has data
		checkDataProviders();


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



		//attach event handler to dash buttons
		DashboardClickListener dBClickListener = new DashboardClickListener();
		findViewById(R.id.dashboard_button_nearme).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_createDrib).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_map).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_help).setOnClickListener(dBClickListener);
	}

	private class DashboardClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = null;
			switch (v.getId()) {
			case R.id.dashboard_button_nearme:
				i = new Intent(DashboardActivity.this, SubjectActivity.class);
				break;
			case R.id.dashboard_button_createDrib:
				i = new Intent(DashboardActivity.this, CreateDribActivity.class);
				break;
			case R.id.dashboard_button_map:
				i = new Intent(DashboardActivity.this, MapsActivity.class);
				break;
			case R.id.dashboard_button_help:
				i = new Intent(DashboardActivity.this, SubjectActivity.class);
				break;
			default:
				break;
			}
			if(i != null) {
				startActivity(i);
			}
		}
	}

	private void checkDataProviders(){


		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {

			Toast.makeText(this, "Network is enabled" , Toast.LENGTH_LONG).show();

		}
		else{

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("A working data connection is not available. Please enable data connection")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					startActivityForResult(intent, 1);
					DashboardActivity.this.finish();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					Toast toast = Toast.makeText(DashboardActivity.this, "Please note that you need location to use Dribble ",Toast.LENGTH_LONG);
					toast.show();
					DashboardActivity.this.finish();
				}
			});
			builder.show();

		}



		//		if( wifi.isAvailable() ){
		//			Toast.makeText(this, "Wifi is enabled" , Toast.LENGTH_LONG).show();
		//		}
		//		else if( mobile.isAvailable() ){
		//			Toast.makeText(this, "mobile data is enabled " , Toast.LENGTH_LONG).show();
		//		}
		//		else
		//		{Toast.makeText(this, "No Network " , Toast.LENGTH_LONG).show();}
	}



	private void checkLocationProviders(){
		Log.i(TAG,"checking available Location providers");
		//String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

			Toast toast = Toast.makeText(DashboardActivity.this, "GPS provider Enabled: ",Toast.LENGTH_LONG);
			toast.show();


		}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

			Toast toast = Toast.makeText(DashboardActivity.this, "Network provider Enabled: ",Toast.LENGTH_LONG);
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
					DashboardActivity.this.finish();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					Toast toast = Toast.makeText(DashboardActivity.this, "Please note that you need an active data connection to use Dribble ",Toast.LENGTH_LONG);
					toast.show();
					DashboardActivity.this.finish();
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
