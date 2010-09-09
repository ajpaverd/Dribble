package dribble.dribbleapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MapsThread implements Runnable,
		LocationListener {

	public static int LATITUDE;
	public static int LONGITUDE;

	public void run() {
		Log.i("Thread Running", "Location listening thread");
		while (true) {
			try {
				Thread.sleep(60000);
				Log.i("Location Thread", "Latitude: " + LATITUDE + " , "
						+ "Latitude: " + LONGITUDE);
			} catch (InterruptedException e) {
				Log.e("Error", e.getMessage());
			}
		}
	}

	public void onLocationChanged(Location location) {
		LATITUDE = (int) (location.getLatitude() * 1E6);
		LONGITUDE = (int) (location.getLongitude() * 1E6);
		Log.i("Location change", "" + LATITUDE + " : " + LONGITUDE);
	}

	public void onProviderDisabled(String provider) {

	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
