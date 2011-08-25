package com.dribble.dribbleapp;

import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BroadcastGpsTask extends TimerTask{

	private Bundle gpsBundle;
	private Context context;
	
	//Create an intent to broadcast the measurements
	private Intent broadcastMeasurements = new Intent(
			DribbleMain.BROADCAST_GEOGRAPHIC_MEASUREMENTS);
	
	public BroadcastGpsTask(Context context, Bundle gpsBundle)
	{
		this.context = context;
		this.gpsBundle = gpsBundle;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		broadcastMeasurements.putExtras(gpsBundle);
		context.sendBroadcast(broadcastMeasurements);
	}

}
