// Authors: Dribble
// Date: 24 April 2010
// Class: MapItemizedOverlay

package com.dribble.dribbleapp;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

// Class to add overlay items to Google map
public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private static ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private static Context mContext;

	public MapItemizedOverlay(Drawable defaultMarker) 
	{
		// draws marker like a "pin"
		super(boundCenterBottom(defaultMarker));
	}

	public MapItemizedOverlay(Drawable defaultMarker, Context context) 
	{
		super(boundCenterBottom(defaultMarker));
		mContext = context;
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

	// Show title of overlay on tap
	@Override
	public boolean onTap(int index) 
	{
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
}