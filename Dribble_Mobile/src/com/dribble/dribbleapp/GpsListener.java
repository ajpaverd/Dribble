package com.dribble.dribbleapp;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/* 
 Location listener class, should update to use method described in 
 http://android-developers.blogspot.com/2011/06/deep-dive-into-location.html
 and http://blog.radioactiveyak.com/2011/06/deep-dive-into-location-part-2-being.html
 */
public class GpsListener extends Activity implements LocationListener
{
	static String provider;
	static LocationManager locationManager;
	static Criteria criteria = new Criteria();
	// minimum time between updates (milliseconds)
	static final int minTime = 6000;
	// minimum distance required between updates (meters)
	static final int minDistance = 50;
	Context mContext;
	static final String TAG = "GPS LISTENER";
	
	public GpsListener(Context mContext)
	{
		this.mContext = mContext;
		Log.i("Thread Running", "Location listening thread");
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			Log.i(TAG,"GPS Location avaialble");
			provider = LocationManager.NETWORK_PROVIDER;
			
		}
		else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Log.i(TAG,"Network location Available");
			provider = LocationManager.GPS_PROVIDER;
		}
		locationManager.getLastKnownLocation(provider);
		
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, minTime, minDistance, this);			
		}
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, minTime*10, minDistance, this);
		}
	}

	// Get best location based on accuracy and time, otherwise just most recent
	public static Location getLocation()
	{
		Location bestResult = null;
		long bestTime = Long.MAX_VALUE;
		float bestAccuracy = Float.MAX_VALUE;
		
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders)
		{
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null)
			{
				float accuracy = location.getAccuracy();
				long time = System.currentTimeMillis() - location.getTime();

				if ((time > minTime && accuracy < bestAccuracy))
				{
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				}
				else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime)
				{
					bestResult = location;
					bestTime = time;
				}
			}
		}
		
		if (bestResult == null)
		{
			Criteria criteria = new Criteria();
			return locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
		}
		
		return bestResult;
	}

	public void onLocationChanged(Location location)
	{
		Log.i("Location change",
				"" + location.getLatitude() + " : " + location.getLongitude());
	}

	public void onProviderDisabled(String provider)
	{
	}

	public void onProviderEnabled(String provider)
	{

	}

	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}
}
