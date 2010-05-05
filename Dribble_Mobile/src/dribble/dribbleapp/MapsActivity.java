// Authors: Dribble
// Date: 24 April 2010
// Class: MapsActivity

package dribble.dribbleapp;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import dribble.dribbleapp.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapsActivity extends MapActivity {
    /** Called when the activity is first created. */
	
	 MyLocationOverlay myLocOverlay;
     MapController mapController;
     MapView mapView;
     private static final String TAG = "MapsActivity";
     public static MapItemizedOverlay Itemizedoverlay;
     public static int LATITUDE;
     public static int LONGTIUDE;
      
    @Override
    protected boolean isRouteDisplayed() {
   
        return false;
    }    
    
    private void initLocationManager() {
    	
    	LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     	LocationListener locListener = new LocationListener() {    		
    		
			public void onLocationChanged(Location newLocation) {
				// transform the location to a geopoint
				GeoPoint geopoint = new GeoPoint(
						(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
								.getLongitude() * 1E6));

				mapController.animateTo(geopoint);
				mapView.invalidate();
			}
 
			public void onProviderDisabled(String arg0) {
			}
 
			public void onProviderEnabled(String arg0) {
			}
 
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};
		Log.i(TAG, "Location Listener Initialised");
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListener); 
	}
     
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Tab Loaded");
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.dribicon);
        Itemizedoverlay = new MapItemizedOverlay(drawable, this);
        
        myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapOverlays.add(myLocOverlay);		
		mapController = mapView.getController();
		
		final Thread myThread = new Thread(new MapsThread());
		myLocOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	LATITUDE = myLocOverlay.getMyLocation().getLatitudeE6();
            	LONGTIUDE = myLocOverlay.getMyLocation().getLongitudeE6();
                mapController.animateTo(myLocOverlay.getMyLocation());
                mapController.setCenter(myLocOverlay.getMyLocation());
                mapController.setZoom(16); 
                
                myThread.start();
                
            }
        }); 		 
		
	    GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem2 = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        Itemizedoverlay.addOverlay(overlayitem2);
        
        GeoPoint point2 = new GeoPoint(-26191794, 28027023);
        OverlayItem overlayitem3 = new OverlayItem(point2, "Dribble HQ", "Smart app built by smart people");
        Itemizedoverlay.addOverlay(overlayitem3);
        
        mapOverlays.add(Itemizedoverlay);
        Log.i(TAG, "Add Itemized Overlay onto Map");                
    }
    
    protected void onDestroy()
    {
       super.onDestroy();
       
       // After this is called, your app process is no longer available in DDMS
       android.os.Process.killProcess(android.os.Process.myPid());
    }    
   
    
 }


