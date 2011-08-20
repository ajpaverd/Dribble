// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: CreateDribActivity

package com.dribble.dribbleapp;

import com.dribble.common.Drib;
import com.dribble.common.DribSubject;

import com.dribble.dribbleapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class CreateDribActivity extends Activity
{
	private static final String TAG = "CreateDribActivity";
	private static DribSubject dribSubject;
	// private static ProgressDialog pd;
	private static Handler mHandler = new Handler();
	private Context context;
	
	public CreateDribActivity()
	{
	}

	public CreateDribActivity(Context context)
	{
		this.context = context;
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");

		setContentView(R.layout.input_drib);
	}

	// Refresh content for creating new dribs
	//
	public void refreshContent()
	{
		final EditText dribTopic = (EditText) findViewById(R.id.topicInput);

		// Submit button event
		Button buttonSubmit = (Button) findViewById(R.id.submit);
		buttonSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Log.i(TAG, "Button Listener Activated (Button Clicked)");

				// Get topic and name from view
				String dribTopicName = dribTopic.getText().toString();

				// Make sure all inputs are filled in
				if (dribTopicName.equals(""))
				{
					// Show error message
					new AlertDialog.Builder(CreateDribActivity.this)
							.setTitle("Error")
							.setMessage("Topic cannot be empty")
							.setPositiveButton("OK", null).show();
				}
				else
				{
					// Create Topic for new Drib
					Location loc = GpsListener.getLocation();
					dribSubject = new DribSubject(dribTopicName, loc.getLatitude(), loc.getLongitude());

					EditText dribMessage = (EditText) findViewById(R.id.dribInput);
					String dribText = dribMessage.getText().toString();
					
					// Send drib for a subject
					sendDrib(dribSubject, dribText);
				}
			}
		});
	}
	
	public void sendDrib(DribSubject subject, String message)
	{		
		if (message.equals(""))
		{
			// Show error message
			new AlertDialog.Builder(CreateDribActivity.this)
					.setTitle("Error")
					.setMessage("Drib cannot be empty")
					.setPositiveButton("OK", null).show();
		}
		else
		{
			// Data is ok

			// Create new drib
			Location loc = GpsListener.getLocation();
			final Drib newDrib = new Drib(subject, message, loc.getLatitude(), loc.getLongitude());
			Log.i(TAG, "Submit new message");

			// Ignoring progress dialog for now, might look better
			// without it - Chad
			// pd = new ProgressDialog(v.getContext());
			// pd.setMessage("Sending Drib...");
			// pd.setIndeterminate(true);
			// pd.setCancelable(true);
			// pd.show();

			// Create a new Thread and send Drib
			Thread sendDrib = new Thread()
			{
				public void run()
				{
					// send drib
					DribCom.sendDrib(newDrib);
					// Call this method once action is complete
					mHandler.post(mUpdateResults);
				}
			};
			sendDrib.start();
		}
	}

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable()
	{
		public void run()
		{
			// update final results in main (UI) Thread
			updateResultsInUi();
		}
	};

	private void updateResultsInUi()
	{
		// dismiss progress dialog
		// pd.dismiss();

		Log.i(TAG, "Message Successfully Submitted");
		
		// Create toast (popup) to show send complete
		Toast success = Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT);
		success.show();
		
		context.sendBroadcast(new Intent("com.dribble.dribbleapp.SENT_DRIB"));

		// Set tab to first tab (subjects)
		TabActivity tabActivity = (TabActivity) getParent();
		if (tabActivity != null)
		{
			
			TabHost tabHost = tabActivity.getTabHost();
			tabHost.setCurrentTab(0);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		context = this;
		refreshContent();
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// Show hidden items and clear text
		EditText et = (EditText) findViewById(R.id.topicInput);
		et.setText("");
		EditText drib = (EditText) findViewById(R.id.dribInput);
		drib.setText("");
	}
}