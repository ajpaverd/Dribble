package com.dribble.dribbleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DashboardActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dashboard);
	 
	    //attach event handler to dash buttons
	    DashboardClickListener dBClickListener = new DashboardClickListener();
	    findViewById(R.id.dashboard_button_nearme).setOnClickListener(dBClickListener);
	    findViewById(R.id.dashboard_button_createDrib).setOnClickListener(dBClickListener);
	    findViewById(R.id.dashboard_button_map).setOnClickListener(dBClickListener);
	    findViewById(R.id.dashboard_button_help).setOnClickListener(dBClickListener);
	}
	
	private class DashboardClickListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	        Intent i = null;
	        switch (v.getId()) {
	            case R.id.dashboard_button_nearme:
	                i = new Intent(DashboardActivity.this, SubjectActivity.class);
	                break;
	            case R.id.dashboard_button_createDrib:
	                i = new Intent(DashboardActivity.this, CreateDribActivity.class);
	                break;
	            case R.id.dashboard_button_map:
	                i = new Intent(DashboardActivity.this, MapsActivity.class);
	                break;
	            case R.id.dashboard_button_help:
	                i = new Intent(DashboardActivity.this, SubjectActivity.class);
	                break;
	            default:
	                break;
	        }
	        if(i != null) {
	            startActivity(i);
	        }
	    }
	}
	
}
