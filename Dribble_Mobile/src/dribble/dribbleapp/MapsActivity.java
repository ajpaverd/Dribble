// Authors: Dribble
// Date: 24 April 2010
// Class: MapsActivity

package dribble.dribbleapp;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

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

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onResume() {
		super.onResume();
		if (myLocOverlay.getLastFix() != null) {
			GeoPoint geopoint = new GeoPoint(MapsThread.LATITUDE,
					MapsThread.LONGITUDE);
			mapController.animateTo(geopoint);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");
		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources()
				.getDrawable(R.drawable.drib_icon_pushpin);
		Itemizedoverlay = new MapItemizedOverlay(drawable, this);

		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapOverlays.add(myLocOverlay);
		mapController = mapView.getController();

		final Thread myThread = new Thread(new MapsThread(), "MapsThread");
		myLocOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				MapsThread.LATITUDE = myLocOverlay.getMyLocation()
						.getLatitudeE6();
				MapsThread.LONGITUDE = myLocOverlay.getMyLocation()
						.getLongitudeE6();
				mapController.animateTo(myLocOverlay.getMyLocation());
				mapController.setCenter(myLocOverlay.getMyLocation());
				mapController.setZoom(16);
				myThread.start();
			}
		});

		GeoPoint point = new GeoPoint(-26191794, 28027023);
		OverlayItem overlayitem = new OverlayItem(point, "Dribble HQ",
				"Chamber of Mines, Wits University");
		Itemizedoverlay.addOverlay(overlayitem);

		mapOverlays.add(Itemizedoverlay);
		Log.i(TAG, "Add Itemized Overlay onto Map");
	}

	protected void onDestroy() {
		super.onDestroy();
		// After this is called, your app process is no longer available in DDMS
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
