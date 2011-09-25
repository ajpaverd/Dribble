package com.dribble.dribbleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

// Main activity to show initial help text and logo
public class DribbleMain extends Activity {

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,  
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.main);
		
		Button buttonOK = (Button) findViewById(R.id.buttonOK);

		buttonOK.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v)
			{
				DribbleMain.this.finish();	
			}	
		});
	}
	
	
}
