// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: Splash

package com.dribble.dribbleapp;

import java.util.Timer;

import com.dribble.dribbleapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class Splash extends Activity {

	private static final String TAG = "Splash";
	// Splash screen display length (milliseconds)
	private static int SPLASH_DISPLAY_LENGTH = 1500;



	@Override
	public void onCreate(Bundle icicle) 
	{
		super.onCreate(icicle);
		Log.i(TAG, "Splash displayed");
		setContentView(R.layout.splash);

		
		
		/* New Handler to start the Menu-Activity
		 * and close this Splash-Screen after some seconds.*/
		new Handler().postDelayed(new Runnable()
		{
			public void run() 
			{
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(Splash.this,DashboardActivity.class);
				Log.i(TAG, "Start Dribble Activity");
				Splash.this.startActivity(mainIntent);

				Splash.this.finish();
				Log.i(TAG, "Splash Activity Finished");
			}
		}, SPLASH_DISPLAY_LENGTH);
		
	}

	

	
}

