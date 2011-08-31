// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: CreateDribActivity

package com.dribble.dribbleapp;

import com.dribble.common.Drib;
import com.dribble.common.DribSubject;

import com.dribble.dribbleapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class CreateDribActivity extends Activity
{
	private static final String TAG = "CreateDribActivity";
	private static DribSubject dribSubject;
	// private static ProgressDialog pd;
	private static Handler mHandler = new Handler();
	private Context context;
	//Preventing static call
	private GpsListener gpsListener;

	//For receiving geographic measurements
	public GeographicMeasurementsReceiver geographicMeasurementsReceiver;
	public Bundle geographicMeasurementsBundle;
	
	public Location myLoc;

	public CreateDribActivity()
	{
	}

	public CreateDribActivity(Context context)
	{
		this.context = context;
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");

		setContentView(R.layout.input_drib);

		//Create Location Object

		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			myLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			myLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		//Register broadcast receiver


		//Registered Receiver
	}

	// Refresh content for creating new dribs
	//
	public void refreshContent()
	{
		final EditText dribTopic = (EditText) findViewById(R.id.topicInput);

		// Submit button event
		Button buttonSubmit = (Button) findViewById(R.id.submit);
		buttonSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Log.i(TAG, "Button Listener Activated (Button Clicked)");

				// Get topic and name from view
				String dribTopicName = dribTopic.getText().toString();

				// Make sure all inputs are filled in
				if (dribTopicName.equals(""))
				{
					// Show error message
					new AlertDialog.Builder(CreateDribActivity.this)
					.setTitle("Error")
					.setMessage("Topic cannot be empty")
					.setPositiveButton("OK", null).show();
				}
				else
				{
					double latitude = myLoc.getLatitude();
					double longitude = myLoc.getLongitude();
					// Create Topic for new Drib
					dribSubject = new DribSubject(dribTopicName, (int)(latitude*1E6), (int)(longitude*1E6));

					EditText dribMessage = (EditText) findViewById(R.id.dribInput);
					String dribText = dribMessage.getText().toString();

					// Send drib for a subject
					sendDrib(dribSubject, dribText, latitude, longitude);
					
					// Hide soft keyboard
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(dribMessage.getWindowToken(), 0);
				}
			}
		});
	}

	public void sendDrib(DribSubject subject, String message, double latitude, double longitude  )
	{		
		if (message.equals(""))
		{
			// Show error message
			new AlertDialog.Builder(CreateDribActivity.this)
			.setTitle("Error")
			.setMessage("Drib cannot be empty")
			.setPositiveButton("OK", null).show();
		}
		else
		{
			// Data is ok

			// Create new drib
			Log.i(TAG,"New Drib Latitude " + latitude);
			Log.i(TAG,"New Drib Longitude " + longitude);
			final Drib newDrib = new Drib(subject, message, (int)(latitude *1E6), (int)(longitude * 1E6));
			Log.i(TAG, "Submit new message");

			// Create a new Thread and send Drib
			Thread sendDrib = new Thread()
			{
				public void run()
				{
					// send drib
					DribCom.sendDrib(newDrib);
					// Call this method once action is complete
					mHandler.post(mUpdateResults);
				}
			};
			sendDrib.start();
		}
	}

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable()
	{
		public void run()
		{
			// update final results in main (UI) Thread
			updateResultsInUi();
		}
	};

	private void updateResultsInUi()
	{
		EditText dribMessage = (EditText) findViewById(R.id.dribInput);
		// Hide soft keyboard
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(dribMessage.getWindowToken(), 0);

		Log.i(TAG, "Message Successfully Submitted");

		// Create toast (popup) to show send complete
		Toast success = Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT);
		success.show();

		context.sendBroadcast(new Intent("com.dribble.dribbleapp.SENT_DRIB"));

		//TODO Return to dashboard screen
		Intent dashboard = new Intent(this, DashboardActivity.class);
		dashboard.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(dashboard);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//Register broadcast receiver
				geographicMeasurementsReceiver = new GeographicMeasurementsReceiver(myLoc);
				this.registerReceiver(geographicMeasurementsReceiver, 
						new IntentFilter(DashboardActivity.BROADCAST_GEOGRAPHIC_MEASUREMENTS));
		context = this;
		refreshContent();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(geographicMeasurementsReceiver);
		// Show hidden items and clear text
		EditText et = (EditText) findViewById(R.id.topicInput);
		et.setText("");
		EditText drib = (EditText) findViewById(R.id.dribInput);
		drib.setText("");
		
		unregisterReceiver(geographicMeasurementsReceiver);
	}
}