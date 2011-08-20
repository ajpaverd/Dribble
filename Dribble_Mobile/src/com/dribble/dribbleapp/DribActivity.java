// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: DribActivity

package com.dribble.dribbleapp;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.dribble.common.Drib;

import com.dribble.dribbleapp.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class DribActivity extends ListActivity {

	private static final String TAG = "DribActivity";
	
	private static ProgressDialog pd;
	private static ArrayList<Drib> messageList;
	private static int subjectID;
	private static String subjectName;
	
	private static Location myLoc;

	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");
		setContentView(R.layout.messages);

		// disable reply button by default if no messages
		//
		Button buttonReply = (Button) findViewById(R.id.buttonReply);
		EditText replyEditText = (EditText) findViewById(R.id.replyDrib);
		buttonReply.setEnabled(false);
		replyEditText.setEnabled(false);
		
		buttonReply.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				// Send reply drib
				//
				EditText replyEditText = (EditText) findViewById(R.id.replyDrib);
				String replyText = replyEditText.getText().toString();
				
				CreateDribActivity createDrib = new CreateDribActivity();
				createDrib.sendDrib(SubjectActivity.CurrentDribSubject, replyText);
				replyEditText.setText("");
			}
		});
	}

	private void refreshContent() 
	{
		// Show progress dialog
		pd = new ProgressDialog(this);
		pd.setMessage("Retrieving Dribs...");
		pd.setIndeterminate(true);
		pd.setCancelable(true);
		pd.show();
		
		// Get current location
	    myLoc = GpsListener.getLocation();
		
		final int results = DribbleSharedPrefs.getNumDribTopics(this);

		Thread getDribs = new Thread()
		{
			public void run() 
			{
				// Get selected subjectID and name and return list of associated messages
				//
				subjectID = SubjectActivity.CurrentDribSubject.getSubjectID();
				subjectName = SubjectActivity.CurrentDribSubject.getName();
				messageList = DribCom.getMessages(subjectID, results);
				Log.i(TAG, "Tab Loaded HERE");

				mHandler.post(mUpdateResults);
			}
		};
		getDribs.start();
	}

	// Create runnable for posting
	//
	final Runnable mUpdateResults = new Runnable() 
	{
		public void run() 
		{
			// Update results in main (UI) Thread
			//
			updateResultsInUi();
		}
	};

	private void updateResultsInUi() 
	{
		TextView messageText = (TextView) findViewById(R.id.topicNameForMessages);
		if (messageList.isEmpty()) 
		{
			messageText.setText("Topics");
		} else 
		{
			// Get Topic Name from TabActivity Tab
			messageText.setText(subjectName);
		}

		setListAdapter(new DribAdapter(getApplicationContext(), R.layout.message_row, messageList));

		Log.i(TAG, "Topic Messages Added To Display");
		
		// dismiss progress dialog
		//
		pd.dismiss();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		
		// If a subject is selected, re-enable buttons
		//
		if (SubjectActivity.CurrentDribSubject != null) 
		{
			refreshContent();
			
			Button buttonReply = (Button) findViewById(R.id.buttonReply);
			EditText replyEditText = (EditText) findViewById(R.id.replyDrib);
			buttonReply.setEnabled(true);
			replyEditText.setEnabled(true);
		}
	}

	// Class to hold custom list view information
	//
	private static class ViewHolder 
	{
		TextView message;
		TextView info;
		ImageButton like;
		ImageButton dislike;
	}

	// Show elapsed time since post
	private String getElapsed(long millis) 
	{
		long time = millis / 1000;
		//String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		int tempHours = (int) (time / 3600);
		String days = Integer.toString(tempHours / 24);
		String hours = Integer.toString(tempHours%24);

		minutes += "min ";
		if (hours.equals("0")) {
			hours = "";
		} else {
			hours += "hrs ";
		}
		if (days.equals("0")) {
			days = "";
		} else {
			days += "days ";
		}

		return days + hours + minutes;
	}

	// Custom list view implementation
	//
	private class DribAdapter extends ArrayAdapter<Drib> {

		private ArrayList<Drib> items;

		// Set list items
		public DribAdapter(Context context, int textViewResourceId, ArrayList<Drib> items)
		{
			super(context, textViewResourceId, items);
			this.items = items;
		}

		// Override default list view
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			final ViewHolder holder;
			if (convertView == null) 
			{
				LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.message_row, null);

				holder = new ViewHolder();
				holder.message = (TextView) convertView.findViewById(R.id.textmsg);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.like = (ImageButton) convertView.findViewById(R.id.buttonlike);
				holder.dislike = (ImageButton) convertView.findViewById(R.id.buttondislike);

				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}

			final Drib drib = items.get(position);

			// Set drib like count
			if (drib != null)
			{
				convertView.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v) 
					{
						Intent maps = new Intent(DribActivity.this, MapsActivity.class);
						DribActivity.this.startActivity(maps);
					}
				});

				holder.like.setOnClickListener(new OnClickListener() 
				{
					public void onClick(View v) 
					{
						// Increase like count
						drib.setLikeCount(drib.getLikeCount() + 1);
						new Thread(new Runnable()
						{
							public void run()
							{
								// send drib like
								DribCom.sendDrib(drib);
							}
						}).start();

						holder.like.setEnabled(false);
						holder.dislike.setEnabled(false);
					}
				});

				// set drib dislike
				holder.dislike.setOnClickListener(new OnClickListener() 
				{
					public void onClick(View v) 
					{
						// increase dislike
						drib.setLikeCount(drib.getLikeCount() - 1);
						new Thread(new Runnable() 
						{
							public void run() 
							{
								// send displike
								DribCom.sendDrib(drib);
							}
						}).start();
						holder.like.setEnabled(false);
						holder.dislike.setEnabled(false);
					}
				});

				holder.message.setText(drib.getText());
				
				Location dribLoc = new Location("Drib Location");
				dribLoc.setLatitude(drib.getLatitude());
				dribLoc.setLongitude(drib.getLongitude());
				
				// Get distance in km
				double distance = myLoc.distanceTo(dribLoc)/1000;
				DecimalFormat df = new DecimalFormat("#.##"); 
				
				// Set info text
				holder.info.setText(df.format(distance) + " km " + "("+ getElapsed(System.currentTimeMillis() - drib.getCurrentTime()) + "ago)" ); // + "\nDRank: " + drib.getPopularity());
			}
			return convertView; // return custom view
		}
	}

}
