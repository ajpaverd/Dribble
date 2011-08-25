package com.dribble.dribbleapp;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/* 
 Location listener class, should update to use method described in 
 http://android-developers.blogspot.com/2011/06/deep-dive-into-location.html
 and http://blog.radioactiveyak.com/2011/06/deep-dive-into-location-part-2-being.html
 */
public class GpsListener implements LocationListener,
		GpsStatus.Listener
{
	
	static String provider;
	public LocationManager locationManager;
	private Location loc;
	static Location currentLocation;
	static Criteria criteria = new Criteria();
	// minimum time between updates (milliseconds)
	static final int minTime = 60000;
	// minimum distance required between updates (meters)
	static final int minDistance = 50;
	Context mContext;
	
	//Declare bundle for Geographic Measurements
	private Bundle gpsBundle;
	
	private static final String TAG = "GPSListener";
	
	public GpsListener(Context mContext, Bundle gpsBundle)
	{
		this.mContext = mContext;
		//Call the gpsBundle into this class
		this.gpsBundle = gpsBundle;
		
		Log.i("Thread Running", "Location listening thread");
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);
		
//		checkLocationProviders();

		//if (DribbleSharedPrefs.getUseGPS(mContext))
		//{
		
//		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//		{
//			
//			provider = LocationManager.GPS_PROVIDER;
//			Log.i(TAG,"Provider is changed to GPS");
//		}
//		else
//			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//		{
//			
//			provider = LocationManager.NETWORK_PROVIDER;
//			Log.i(TAG,"Provider is changed to Network");
//		}
//		else
//			{
//			Log.i(TAG,LocationManager.NETWORK_PROVIDER);
//			Log.i(TAG,"Enabled/disabled = "+locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
//				Log.i(TAG,"No Provider Available");
//			}
		
		//Criteria criteria = new Criteria();
		//provider = locationManager.getBestProvider(criteria, false);
//		if(locationManager.getLastKnownLocation(provider)!=null)
//		{
//			Log.i(TAG,"Provider "+provider+" is enabled");
//			loc = locationManager.getLastKnownLocation(provider);
//		}
//		else
//		{
//			Log.i(TAG,"No providers have been enabled");
//			loc.setLatitude(-22.00);
//			loc.setLongitude(23.00);
//		}
//		
//		if(provider!=null)
//		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}


	// Get best location based on accuracy and time, otherwise just most recent
	public Location getLocation()
	{
		//Get Location by giving user last known location and then 	
		Log.i(TAG,"get last known location");
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Log.i(TAG,"GPS Location avaialble");
			
		
		}
		else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			Log.i(TAG,"Network location Available");
//			currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
		}
		else
		{
			Log.i(TAG,"No Network Provider Available");
			currentLocation.setLatitude(22.00);
			currentLocation.setLongitude(-23.00);
			
		}
		
		return currentLocation;
//		Location bestResult = null;
//		long bestTime = Long.MAX_VALUE;
//		float bestAccuracy = Float.MAX_VALUE;
//		
//		List<String> matchingProviders = locationManager.getAllProviders();
//		for (String provider : matchingProviders)
//		{
//			Location location = locationManager.getLastKnownLocation(provider);
//			if (location != null)
//			{
//				float accuracy = location.getAccuracy();
//				long time = System.currentTimeMillis() - location.getTime();
//
//				if ((time > minTime && accuracy < bestAccuracy))
//				{
//					bestResult = location;
//					bestAccuracy = accuracy;
//					bestTime = time;
//				}
//				else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime)
//				{
//					bestResult = location;
//					bestTime = time;
//				}
//			}
//		}
//		
//		if (bestResult == null)
//		{
//			Criteria criteria = new Criteria();
//			return locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//		}
//		
//		return bestResult;
	}
	
	public void onLocationChanged(Location location)
	{
		//TODO Get best location based on time and accuracy
		Log.i("Location change",
				"" + location.getLatitude() + " : " + location.getLongitude());
		
		if (location!=null)
		{
			//update the bundle with latitude and longitude measurements
			gpsBundle.putDouble("myLatitude",location.getLatitude());
			gpsBundle.putDouble("myLongitude",location.getLongitude());
			
		}
	}

	public void onProviderDisabled(String provider)
	{
//		if (DribbleSharedPrefs.getUseGPS(mContext))
//		{
//			provider = locationManager.GPS_PROVIDER;
//		}
//		else
//		{
//			// find new best provider
//			provider = locationManager.NETWORK_PROVIDER;
//		}
//		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}

	public void onProviderEnabled(String provider)
	{
//		// find new best provider
//		if (DribbleSharedPrefs.getUseGPS(mContext))
//		{
//			provider = locationManager.GPS_PROVIDER;
//		}
//		else
//		{
//			provider = locationManager.NETWORK_PROVIDER;
//		}
//
//		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}
	
	public void enableGPSLocation() {

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 2, this);
		
		}

	}

	public void enableNetworkLocation() {

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}

	}
	
	
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	public void onGpsStatusChanged(int event)
	{

	}
}
