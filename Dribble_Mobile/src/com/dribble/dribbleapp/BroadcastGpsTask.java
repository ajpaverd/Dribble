package com.dribble.dribbleapp;

import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BroadcastGpsTask extends TimerTask{

	private Bundle locationBundle;
	private Context context;
	public static final String TAG  = "BroadcastGpsTask";
	//Create an intent to broadcast the measurements
	private Intent broadcastMeasurements = new Intent(
			DashboardActivity.BROADCAST_GEOGRAPHIC_MEASUREMENTS);
	
	public BroadcastGpsTask(Context context, Bundle locationBundle)
	{
		this.context = context;
		this.locationBundle = locationBundle;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.i(TAG, "Broadcasting location measurements");
		broadcastMeasurements.putExtras(locationBundle);
		context.sendBroadcast(broadcastMeasurements);
	}

}
