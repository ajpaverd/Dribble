// Authors: Dribble
// Date: 24 April 2010
// Class: MessageActivity

package dribble.dribbleapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import dribble.common.Drib;

public class DribActivity extends ListActivity {

	private static final String TAG = "MessageActivity";
	private static ProgressDialog pd;
	private static ArrayList<Drib> messageList;
	private static int subjectID;
	private static String subjectName;

	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");
		setContentView(R.layout.messages);

		Button buttonReply = (Button) findViewById(R.id.buttonreply);
		buttonReply.setEnabled(false);

		buttonReply.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CreateDribActivity.newMessage = false;
				Intent mainIntent = new Intent(DribActivity.this,
						CreateDribActivity.class);
				Log.i(TAG, "Start Dribble Activity");
				DribActivity.this.startActivity(mainIntent);
			}
		});
	}

	private void refreshContent() {
	//	pd = new ProgressDialog(this);
	//	pd.setMessage("Retrieving Dribs...");
	//	pd.setIndeterminate(true);
	//	pd.setCancelable(true);
	//	pd.show();

		Thread getDribs = new Thread() {
			public void run() {
				subjectID = SubjectActivity.SubjectID;
				subjectName = SubjectActivity.SubjectName;
				messageList = DribCom.getMessages(subjectID);
				Log.i(TAG, "Tab Loaded HERE");

				mHandler.post(mUpdateResults);
			}
		};
		getDribs.start();
	}

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}
	};

	private void updateResultsInUi() {
		TextView messageText = (TextView) findViewById(R.id.topicNameForMessages);
		if (messageList.isEmpty()) {
			messageText.setText("Topics");
		} else {
			// Get Topic Name from TabActivity Tab
			messageText.setText(subjectName);
		}
		Log.i(TAG, "Topic name set");

		setListAdapter(new DribAdapter(getApplicationContext(),
				R.layout.message_row, messageList));

		Log.i(TAG, "Topic Messages Added To Display");
		
		// pd.dismiss();
	}

	@Override
	public void onResume() {
		super.onResume();
		Button buttonReply = (Button) findViewById(R.id.buttonreply);
		if (SubjectActivity.SubjectID != -1) {
			refreshContent();
			buttonReply.setEnabled(true);
		}

	}

	private static class ViewHolder {
		TextView message;
		TextView info;
		ImageButton like;
		ImageButton dislike;
	}

	private String getElapsed(long millis) {
		long time = millis / 1000;
		String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		// String days =

		if (seconds.equals("0")) {
			seconds = "";
		} else {
			seconds += "s ";
		}
		if (minutes.equals("0")) {
			minutes = "";
		} else {
			minutes += "min ";
		}
		if (hours.equals("0")) {
			hours = "";
		} else {
			hours += "hrs ";
		}

		return hours + minutes + seconds;

	}

	private class DribAdapter extends ArrayAdapter<Drib> {

		private ArrayList<Drib> items;

		public DribAdapter(Context context, int textViewResourceId,
				ArrayList<Drib> items) {

			super(context, textViewResourceId, items);
			this.items = items;
			Log.i(TAG, "Drib Adapter Used");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			Log.w(TAG, "Override Function - getView Function");
			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.message_row, null);

				holder = new ViewHolder();
				holder.message = (TextView) convertView
						.findViewById(R.id.textmsg);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.like = (ImageButton) convertView
						.findViewById(R.id.buttonlike);
				holder.dislike = (ImageButton) convertView
						.findViewById(R.id.buttondislike);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Drib drib = items.get(position);

			if (drib != null) {
				holder.like.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						drib.setLikeCount(1);
						new Runnable() {
							public void run() {
								DribCom.sendDrib(drib);
							}
						};

						holder.like.setEnabled(false);
						holder.dislike.setEnabled(false);
					}
				});

				holder.dislike.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						drib.setLikeCount(-1);
						new Runnable() {
							public void run() {
								DribCom.sendDrib(drib);
							}
						};
						holder.like.setEnabled(false);
						holder.dislike.setEnabled(false);
					}
				});

				holder.message.setText(drib.getText());
				holder.info.setText("Sent : "
						+ getElapsed(System.currentTimeMillis()
								- drib.getCurrentTime()) + "ago" + "\nDRank: "
						+ drib.getPopularity());

			}

			return convertView;
		}
	}

}
