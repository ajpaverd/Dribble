package dribble.dribbleapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MapsThread extends MapActivity implements Runnable,LocationListener{
	
	 private MyLocationOverlay myLocOverlay;
     private MapView mapView;
     
     public static int LATITUDE;
     public static int LONGITUDE;
     
//     MapsThread()
//     {
//         mapView = new MapView(this, "0usNXcFTYLvKUSt_4ERmhFbYl2UD8_iUrDjlBhw");         
//         myLocOverlay = new MyLocationOverlay(this, mapView);
//     }
     
	@Override
    protected boolean isRouteDisplayed() {
   
        return false;
    }    
	
	public void run() {
	    LATITUDE = MapsActivity.LATITUDE;
	    LONGITUDE = MapsActivity.LONGTIUDE;
		while (true)
		{
			Log.i("Thread Running","Location listening thread");
			try{
				
//				LATITUDE = myLocOverlay.getMyLocation().getLatitudeE6();
//            	LONGITUDE = myLocOverlay.getMyLocation().getLongitudeE6();
				Thread.currentThread().sleep(600000, 0);
				Log.i("Location Thread", "Latitude: " + LATITUDE + " , " + "Latitude: " + LONGITUDE);
				
			}
			catch (InterruptedException e)
			{
				Log.e("Error", e.getMessage());
			}
		}
		
	}
	
	 public void onLocationChanged(Location location) {
	        LATITUDE = (int)(location.getLatitude()*1E6);
	        LONGITUDE = (int)(location.getLongitude()*1E6);
	        Log.i("Location change",""+LATITUDE+ " : "+LONGITUDE) ;
	    }
	    
	    public void onProviderDisabled(String provider) {
	       
	    }
	    
	    public void onProviderEnabled(String provider) {
	       
	    }
	    
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	       
	    }
	
	
}
