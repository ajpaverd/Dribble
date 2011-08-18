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
public class GpsListener extends Activity implements LocationListener,
		GpsStatus.Listener
{

	static String provider;
	static LocationManager locationManager;
	static Criteria criteria = new Criteria();
	// minimum time between updates (milliseconds)
	static final int minTime = 60000;
	// minimum distance required between updates (meters)
	static final int minDistance = 50;
	Context mContext;
	
	public GpsListener(Context mContext)
	{
		this.mContext = mContext;
		Log.i("Thread Running", "Location listening thread");
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);

		// Ignoring GPS Preference for now
		if (DribbleSharedPrefs.getUseGPS(mContext))
		{
			provider = locationManager.GPS_PROVIDER;
		}
		else
		{
			provider = locationManager.NETWORK_PROVIDER;
		}
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
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
		return bestResult;
	}

	public void onLocationChanged(Location location)
	{
		Log.i("Location change",
				"" + location.getLatitude() + " : " + location.getLongitude());
	}

	public void onProviderDisabled(String provider)
	{
		if (DribbleSharedPrefs.getUseGPS(mContext))
		{
			provider = locationManager.GPS_PROVIDER;
		}
		else
		{
			// find new best provider
			provider = locationManager.NETWORK_PROVIDER;
		}
		locationManager.requestLocationUpdates(provider, minTime, minDistance,
				this);
	}

	public void onProviderEnabled(String provider)
	{
		// find new best provider
		if (DribbleSharedPrefs.getUseGPS(mContext))
		{
			provider = locationManager.GPS_PROVIDER;
		}
		else
		{
			provider = locationManager.NETWORK_PROVIDER;
		}

		locationManager.requestLocationUpdates(provider, minTime, minDistance,
				this);
	}

	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	public void onGpsStatusChanged(int event)
	{

	}
}
