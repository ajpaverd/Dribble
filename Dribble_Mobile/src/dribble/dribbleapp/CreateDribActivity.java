// Authors: Dribble
// Date: 24 April 2010
// Class: DribbleTabs

package dribble.dribbleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
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
import dribble.common.Drib;
import dribble.common.DribSubject;

public class CreateDribActivity extends Activity {
	private static final String TAG = "CreateDribActivity";
	public static boolean newMessage;
	private static DribSubject dribSubject;
	private static ProgressDialog pd;
	private Handler mHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Tab Loaded");
		newMessage=true;
		setContentView(R.layout.input_drib);
	}

	public void refreshContent() {
		EditText et = (EditText) findViewById(R.id.topicInput);
		if (newMessage == false) {
			dribSubject = SubjectActivity.CurrentDribSubject;
			et.setVisibility(EditText.GONE);
		}
		Button buttonSubmit = (Button) findViewById(R.id.submit);
		buttonSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "Button Listener Activated (Button Clicked)");
				
				EditText dribTopic = (EditText) findViewById(R.id.topicInput);
				String dribTopicName = dribTopic.getText().toString();
				EditText dribMessage = (EditText) findViewById(R.id.dribInput);
				String dribText = dribMessage.getText().toString();

				if ((dribTopicName.equals("") && newMessage)
						|| dribText.equals("")) {
					new AlertDialog.Builder(CreateDribActivity.this).setTitle(
							"Error").setMessage("Inputs Can't be Empty")
							.setPositiveButton("OK", null).show();
				} else {
					
					if (newMessage)
					{
						dribSubject = new DribSubject(dribTopicName, 0, GpsListener.getLatitude(), GpsListener.getLongitude(), 0,
								0, System.currentTimeMillis(), 0);
					}
					final Drib newDrib = new Drib(dribSubject, dribText,
							GpsListener.getLatitude(), GpsListener.getLongitude());
					Log.i(TAG, "Submit new message");

//					pd = new ProgressDialog(v.getContext());
//					pd.setMessage("Sending Drib...");
//					pd.setIndeterminate(true);
//					pd.setCancelable(true);
//					pd.show();

					Thread sendDrib = new Thread() {
						public void run() {

							DribCom.sendDrib(newDrib);
							mHandler.post(mUpdateResults);
						}
					};
					sendDrib.start();
				}

			}
		});
	}

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}
	};

	private void updateResultsInUi() {

		// pd.dismiss();
		
		Log.i(TAG, "Message Successfully Submitted");
		Toast success = Toast.makeText(getApplicationContext(),
				"Message sent successfully", Toast.LENGTH_SHORT);
		success.show();

		TabActivity tabActivity = (TabActivity) getParent();
		if (tabActivity == null) {
			this.finish();
		} else {
			TabHost tabHost = tabActivity.getTabHost();
			tabHost.setCurrentTab(0);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if ((TabActivity) getParent() == null) {
			newMessage = false;
		}
		refreshContent();
	}

	@Override
	public void onPause() {
		super.onPause();
		newMessage = true;
		EditText et = (EditText) findViewById(R.id.topicInput);
		et.setVisibility(EditText.VISIBLE);
		et.setText("");
		EditText drib = (EditText) findViewById(R.id.dribInput);
		drib.setText("");

	}
}