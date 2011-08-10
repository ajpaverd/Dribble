package com.dribble.dribbleapp;

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
public class GpsListener extends Activity implements LocationListener, GpsStatus.Listener {

	static String provider;
    static LocationManager locationManager;
	static Criteria criteria = new Criteria();
	// minimum time between updates (milliseconds)
	static int minTime = 6000;
	// minimum distance required between updates (meters)
	static int minDistance = 2;
    Context mContext;
    
	public GpsListener(Context mContext)
	{
		this.mContext = mContext;
		Log.i("Thread Running", "Location listening thread");
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);
		// Define the criteria how to select the location provider -> use default
		// Defualt criteria - find best provider based on accuracy
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
	
	public static Location getLocation()
	{
		Location location = locationManager.getLastKnownLocation(provider);
	    return location;
	}
	
	// get longitude in millidegress
	public static double getLongitude()
	{
		Location location = locationManager.getLastKnownLocation(provider);
		if (location!=null)
			//return (int)(location.getLongitude() * 1E6);
			return location.getLongitude();
		else
			return 0;
	}
	
	// get latitude in millidegress
	public static double getLatitude()
	{
		Location location = locationManager.getLastKnownLocation(provider);
		if (location!=null)
			//return (int)(location.getLatitude() * 1E6);
		    return location.getLatitude();
		else
			return 0;
	}
	
	public void onLocationChanged(Location location) {
		Log.i("Location change", "" + location.getLatitude() * 1E6 + " : " + location.getLongitude() * 1E6);
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
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
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
		
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public void onGpsStatusChanged(int event) {
		
	}		 
}
