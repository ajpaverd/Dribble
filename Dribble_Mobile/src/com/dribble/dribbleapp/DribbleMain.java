package com.dribble.dribbleapp;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

// Main activity to show initial help text and logo
public class DribbleMain extends Activity {

	//Declare the parameters to check GPS Status
	static String provider;
	static LocationManager locationManager;

	//create the new geographic code using bundles
	private GpsListener gpsListener;
	public static final String BROADCAST_GEOGRAPHIC_MEASUREMENTS = "com.dribble.dribbleapp.gpsMeasurements";
	
	//Create Bundle
	private Bundle gpsBundle;
	private Timer gpsTimer;
	
	public static final String TAG = "DribbleMain";

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Initialise the GPS Bundle to be used -last knoiwn location
		//TODO Add last known location
		initialiseGpsBundle();
		
		//Create the location manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		//				locationManager.addGpsStatusListener(GpsListener.class);

		//check if location providers are available
		checkLocationProviders();


		// Start location listening
		gpsListener = new GpsListener(this, gpsBundle);
		
		//Create a broadcast task to broadcast the measurements every 'x' seconds
		BroadcastGpsTask broadcastTask = new BroadcastGpsTask(this, gpsBundle);
		gpsTimer = new Timer();
		gpsTimer.schedule(broadcastTask, 1000, 5000);
		
		Button buttonEnter = (Button) findViewById(R.id.buttonEnter);

		buttonEnter.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent tabs = new Intent(DribbleMain.this, DribbleTabs.class);
				DribbleMain.this.startActivity(tabs);
			}

		});
	}

	private void checkLocationProviders(){
		Log.i(TAG,"checking available Location providers");
		//String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

			Toast toast = Toast.makeText(DribbleMain.this, "GPS provider Enabled: ",Toast.LENGTH_LONG);
			toast.show();

		}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

			Toast toast = Toast.makeText(DribbleMain.this, "Network provider Enabled: ",Toast.LENGTH_LONG);
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

					Toast toast = Toast.makeText(DribbleMain.this, "Please note that you need location to use Dribble ",Toast.LENGTH_LONG);
					toast.show();
					DribbleMain.this.finish();
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
		gpsBundle = new Bundle();
		//Initialise with Dribble HeadQuarter Location
		gpsBundle.putDouble("myLatitude", -26.191794);
		gpsBundle.putDouble("myLongitude", 28.027023);
	}

}
