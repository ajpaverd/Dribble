package com.dribble.dribbleapp;

import com.dribble.dribbleapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

// Main activity to show initial help text and logo
public class DribbleMain extends Activity {

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Start location listening
		new GpsListener(this);
		
		Button buttonEnter = (Button) findViewById(R.id.buttonEnter);

		buttonEnter.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent tabs = new Intent(DribbleMain.this, DribbleTabs.class);
				DribbleMain.this.startActivity(tabs);
			}

		});
	}
}
