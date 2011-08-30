package com.dribble.dribbleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class GeographicMeasurementsReceiver extends BroadcastReceiver {

	private double latitude;
	private double longitude;
	
	private Location location;
	
	public static final String TAG = "GeographicMeasurementsReceiver";
	
	public GeographicMeasurementsReceiver (Location location){
		this.location = location;
	}

		@Override
		public void onReceive(Context context, Intent intent) {
			latitude = intent.getDoubleExtra("myLatitude", 22.800);
			longitude = intent.getDoubleExtra("myLongitude", -28.074);
			Log.i(TAG,"Geographic Measurements Receiver Called");
			//update the latitudes and longitudes respectively
			location.setLatitude(latitude);
			location.setLongitude(longitude);

		}


	
}
