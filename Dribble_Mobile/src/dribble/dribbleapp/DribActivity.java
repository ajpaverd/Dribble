// Authors: Dribble
// Date: 24 April 2010
// Class: MessageActivity

package dribble.dribbleapp;

import java.util.ArrayList;
import java.util.Date;

import dribble.dribbleapp.R;

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import dribble.common.Drib;

public class DribActivity extends ListActivity {
    
	private static final String TAG = "MessageActivity";
	
	private DribCom dribCom = new DribCom();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Tab Loaded");
        setContentView(R.layout.messages);        
        
        Button buttonReply = (Button)findViewById(R.id.buttonreply);       
        
    	buttonReply.setOnClickListener(new OnClickListener() 
        {
		    public void onClick(View v)
		    {
		    	CreateDribActivity.newMessage=false;
		    	TabActivity tabActivity = (TabActivity)getParent();
		    	TabHost tabHost = tabActivity.getTabHost();
		    	tabHost.setCurrentTab(3);
		    }		    
        });
    }
    
    private void refreshContent()
    {
    	int subjectID = SubjectActivity.SubjectID;
    	String subjectName = SubjectActivity.SubjectName;
    	                   	
        DribCom com = new DribCom();
    	ArrayList<Drib> messageList = com.getMessages(subjectID);
    	Log.i(TAG, "Tab Loaded HERE");
    	
    	TextView messageText = (TextView)findViewById(R.id.topicNameForMessages);
    	if (messageList.isEmpty())
    	{    	
    		messageText.setText("Topics");
    	}
    	else
    	{    	
    	// Get Topic Name from TabActivity Tab
    	messageText.setText(subjectName);
    	}
    	Log.i(TAG, "Topic name set"); 
    	
    	setListAdapter(new DribAdapter(this, R.layout.message_row, messageList));
    	    	 
    	Log.i(TAG, "Topic Messages Added To Display");    	
    }
    
    @Override
    public void onResume() {
        super.onResume();
    	if(SubjectActivity.SubjectID!=-1)
    	{
    		refreshContent();
    	}
    }    
    
private class DribAdapter extends ArrayAdapter<Drib> {

    private ArrayList<Drib> items;

    public DribAdapter(Context context, int textViewResourceId, ArrayList<Drib> items) {
    	
        super(context, textViewResourceId, items);
        this.items = items;
        Log.i(TAG, "Drib Adapter Used");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.w(TAG, "Override Function - getView Function");
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.message_row, null);               
                
            }
            final Drib drib = items.get(position);
                        
            if (drib != null)
            {
            	final ImageButton buttonLike = (ImageButton)v.findViewById(R.id.buttonlike);
            	final ImageButton buttonDislike = (ImageButton)v.findViewById(R.id.buttondislike);
                buttonLike.setOnClickListener(new OnClickListener() 
                {
        		    public void onClick(View v)
        		    {
        		    	drib.setLikeCount(1);
        		    	dribCom.sendDrib(drib);
        		    	buttonLike.setEnabled(false);
        		    	buttonDislike.setEnabled(false);
        		    }        		    
                });
                
                buttonDislike.setOnClickListener(new OnClickListener() 
                {
        		    public void onClick(View v)
        		    {
        		    	drib.setLikeCount(-1);
        		    	dribCom.sendDrib(drib);
        		    	buttonLike.setEnabled(false);
        		    	buttonDislike.setEnabled(false);
        		    }        		    
                });
                
            	TextView message = (TextView)v.findViewById(R.id.textmsg);
            	if (message != null)
            	{
            		message.setText(drib.getText());
            	}
            	TextView timeStamp = (TextView)v.findViewById(R.id.timestamp);
            	if (timeStamp != null)
            	{
            		timeStamp.setText(new Date(drib.getCurrentTime()).toString());
            	} 
            	TextView drank = (TextView)v.findViewById(R.id.drank);
            	if (drank != null)
            	{
            		drank.setText("DRank: " + drib.getPopularity());
            	}  
            	
            }            
            
            return v;
    }
}

}

