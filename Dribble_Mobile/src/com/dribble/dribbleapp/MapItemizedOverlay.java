// Authors: Dribble
// Date: 24 April 2010
// Class: MapItemizedOverlay

package com.dribble.dribbleapp;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

// Class to add overlay items to Google map
	public class MapItemizedOverlay extends BalloonItemizedOverlay<OverlayItem>{

	private static ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private static Context mContext;

	public MapItemizedOverlay(Drawable defaultMarker, MapView mapView) 
	{
		// draws marker like a "pin"
		super(boundCenterBottom(defaultMarker), mapView);
		mContext = mapView.getContext();
	}

	public void addOverlay(OverlayItem overlay) 
	{
		mOverlays.add(overlay);
		populate();
	}

	public void clearOverlays()
	{
		mOverlays.clear();
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
}