package dribble.dribbleapp;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsListener implements LocationListener, GpsStatus.Listener {

	static String provider;
    static LocationManager locationManager;
	static Criteria criteria = new Criteria();
	static int minTime = 6000;
	static int minDistance = 2;
    
	public GpsListener(Context mContext){
			Log.i("Thread Running", "Location listening thread");
	        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			locationManager.addGpsStatusListener(this);
			// Define the criteria how to select the location provider -> use default
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
		  }
	
	public static Location getLocation()
	{
		Location location = locationManager.getLastKnownLocation(provider);
	    return location;
	}
	
	public static int getLongitude()
	{
		Location location = locationManager.getLastKnownLocation(provider);
		if (location!=null)
			return (int)(location.getLongitude() * 1E6);
		else
			return 0;
	}
	
	public static int getLatitude()
	{
		Location location = locationManager.getLastKnownLocation(provider);
		if (location!=null)
			return (int)(location.getLatitude() * 1E6);
		else
			return 0;
	}
	
	public void onLocationChanged(Location location) {
		Log.i("Location change", "" + location.getLatitude() * 1E6 + " : " + location.getLongitude() * 1E6);
	}

	public void onProviderDisabled(String provider) {
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);

	}

	public void onProviderEnabled(String provider) {
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public void onGpsStatusChanged(int event) {
		
		
	}

}
