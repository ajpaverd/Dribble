// Authors: Dribble
// Date: 24 April 2010
// Updated 01/07/2011
// Class: DribActivity

package com.dribble.dribbleapp;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dribble.common.Drib;
import com.dribble.data.DribbleData;
import com.dribble.data.DribbleData.VotedUserData;

public class DribActivity extends ListActivity
{
	private static final String TAG = "DribActivity";

	private static ProgressDialog pd;
	private static ArrayList<Drib> messageList;
	private static int subjectID;
	private static String subjectName;


	//To change from static to non-static
	private GpsListener gpsListener;
	private DribCom dribCom;

	//For receiving geographic measurements
	public GeographicMeasurementsReceiver geographicMeasurementsReceiver;
	public Bundle geographicMeasurementsBundle;

	public Location myLoc;

	//Set up the database for likes and dislikes
	private VotedUserData vud = new VotedUserData();
	private DribbleData dd;


	private Handler mHandler = new Handler();

	//Has this been implemented??
	// Refresh content when send drib broadcast is received
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Refresh content after drib has been sent
			refreshContent();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");
		setContentView(R.layout.messages);

		//Create Location Object
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			myLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			myLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		//Register broadcast receiver
		geographicMeasurementsReceiver = new GeographicMeasurementsReceiver(myLoc);
		this.registerReceiver(geographicMeasurementsReceiver, 
				new IntentFilter(Splash.BROADCAST_GEOGRAPHIC_MEASUREMENTS));
		//Registered Receiver

		//initialise new dribcom object
		dribCom = new DribCom(this);
		
		//Initialise local database
		dd = new DribbleData(DribActivity.this);

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

				// Re-use the create activity methos to send reply
				CreateDribActivity createDrib = new CreateDribActivity(getApplicationContext());
				createDrib.sendDrib(SubjectActivity.CurrentDribSubject, replyText, myLoc.getLatitude(), myLoc.getLongitude());
				replyEditText.setText("");

				// Hide soft keyboard
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(replyEditText.getWindowToken(), 0);
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

		final int results = DribbleSharedPrefs.getNumDribTopics(this);

		Thread getDribs = new Thread()
		{
			public void run()
			{
				// Get selected subjectID and name and return list of associated
				// messages
				//
				subjectID = SubjectActivity.CurrentDribSubject.getSubjectID();
				subjectName = SubjectActivity.CurrentDribSubject.getName();
				messageList = dribCom.getMessages(subjectID, myLoc);
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
		setListAdapter(new DribAdapter(getApplicationContext(), R.layout.message_row, messageList));

		ListView lv = getListView();		  
		lv.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{	
				// Log.i(TAG, "List Item Clicked");
				//Drib drib = ((Drib)(messageList.toArray())[position]);
				Intent maps = new Intent(DribActivity.this, MapsActivity.class);
				DribActivity.this.startActivity(maps);
			}
		});

		TextView messageText = (TextView) findViewById(R.id.topicNameForMessages);
		if ( messageList == null || messageList.isEmpty())
		{
			lv.clearChoices();
			messageText.setText("No Messages");	
		}
		else
		{
			// Get Topic Name from TabActivity Tab
			messageText.setText(subjectName);	

			// dismiss progress dialog
			//
			pd.dismiss();
		}	

		Log.i(TAG, "Topic Messages Added To Display");
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		unregisterReceiver(geographicMeasurementsReceiver);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// disable reply button by default if no messages
		//
		Button buttonReply = (Button) findViewById(R.id.buttonReply);
		EditText replyEditText = (EditText) findViewById(R.id.replyDrib);

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.dribble.dribbleapp.SENT_DRIB");
		registerReceiver(broadcastReceiver, filter);

		// If a subject is selected, re-enable buttons
		//
		if (SubjectActivity.CurrentDribSubject != null)
		{
			refreshContent();

			buttonReply.setEnabled(true);
			replyEditText.setEnabled(true);
			replyEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		}
		else
		{
			messageList = new ArrayList<Drib>();
			subjectName = null;

			buttonReply.setEnabled(false);
			replyEditText.setInputType(InputType.TYPE_NULL);
			replyEditText.setEnabled(false);

			updateResultsInUi();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(pd!=null)
			pd.dismiss();
	}

	// Class to hold custom list view information
	//
	private static class ViewHolder
	{
		TextView message;
		TextView time_created;
		TextView distance;
		TextView likeCount;
		ImageButton like;
		ImageButton dislike;
	}

	// Show elapsed time since post
	private String getElapsed(long millis)
	{
		long time = millis / 1000;
		// String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		int tempHours = (int) (time / 3600);
		String days = Integer.toString(tempHours / 24);
		String hours = Integer.toString(tempHours % 24);
		String sent ="";

		if (!days.equals("0"))
		{
			sent = days + " day(s)";
		}
		else if (!hours.equals("0"))
		{
			sent = hours + " hour(s)";
		}
		else if (!minutes.equals("0"))
		{
			sent = minutes + " min";
		}

		return sent + " ago";
	}

	// Custom list view implementation
	//
	private class DribAdapter extends ArrayAdapter<Drib>
	{

		private ArrayList<Drib> items;

		// Set list items
		public DribAdapter(Context context, int textViewResourceId, ArrayList<Drib> items)
		{
			super(context, textViewResourceId, items);
			//All the Drib messages in the list
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
				holder.time_created = (TextView) convertView.findViewById(R.id.time_created);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
				//holder.likeCount = (TextView) convertView.findViewById(R.id.like_count);
				holder.like = (ImageButton) convertView.findViewById(R.id.buttonlike);
				holder.dislike = (ImageButton) convertView.findViewById(R.id.buttondislike);

				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			final Drib drib = items.get(position);
			//Check the id of the Drib. If it has been liked/dislike, disable onClickListener
			//and set the enabled to true for both
			//boolean votedPreviously = checkLikeDislikeId(String.valueOf(drib.getMessageID()));

			// Set drib like count
			if (drib != null)
			{

				//if(!votedPreviously)
				//{
					holder.like.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							// Increase like count
							drib.setLikeCount(drib.getLikeCount() + 1);

							//Add unique drib id to user phone's database
							String dribId = String.valueOf(drib.getMessageID());
							long time_created = drib.getCurrentTime();

							addLikeDislikeId(dribId, time_created);


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
							//increase dislike
							drib.setLikeCount(drib.getLikeCount() - 1);

							//Add unique drib id to user phone's database
							String dribId = String.valueOf(drib.getMessageID());
							long time_created = drib.getCurrentTime();

							addLikeDislikeId(dribId, time_created);

							new Thread(new Runnable()
							{
								public void run()
								{
									// send dislike
									DribCom.sendDrib(drib);
								}
							}).start();
							holder.like.setEnabled(false);
							holder.dislike.setEnabled(false);
						}
					});
				}//end voted previously conditional
				else
				{
					holder.like.setEnabled(true);
					holder.dislike.setEnabled(true);
				}

				holder.message.setText(drib.getText());
				//holder.likeCount.setText(String.valueOf(drib.getLikeCount()));

				Location dribLoc = new Location("Drib Location");
				dribLoc.setLatitude(drib.getLatitude()/1E6);
				dribLoc.setLongitude(drib.getLongitude()/1E6);

				// Get distance in km
				double distance = myLoc.distanceTo(dribLoc) / 1000;
				DecimalFormat df = new DecimalFormat("#.##");

				// Set info text
				holder.time_created.setText(getElapsed(System.currentTimeMillis() - drib.getCurrentTime()));
				holder.distance.setText(df.format(distance) + "km");
			//}

			return convertView; // return custom view
		}

		public void addLikeDislikeId(String dribId, long timeCreated){

			if (dribId!=null)
			{
				
				//Initialise Cursor
				Cursor dbCursor = dd.getDribId(dribId);

				//If the id does not exist
				if(dbCursor.getCount()==0){

					//Set the vud with the message to be placed in database
					vud.SetVotedUserData(dribId, timeCreated);
					//Place into database
					dd.insert(vud);
				}
				else{
					dbCursor.deactivate();
					dbCursor.close();
				}

			}
			if(dd!=null){
				Log.i(TAG,"Cleaning up database");
				dd.cleanup();
			}

		}

		//Checks if a user has like/disliked a drib
		public boolean checkLikeDislikeId(String dribId){
			//ArrayList<VotedUserData> votedArray = new ArrayList<VotedUserData>();

			
			//Initialise Cursor
			Cursor dbCursor = dd.getDribId(dribId);

			if(dbCursor.getCount()==0){
				return false;
			}
			else
			{
				return true;
			}


		}



	}
}
