// Authors: Dribble
// Date: 24 April 2010
// Class: Drib

package com.dribble.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.util.Log;

	@Root (name="com.dribble.common.Drib")
	public class Drib {
	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */
		private static final String TAG = "Drib";
		@Element
	    private String text;
		@Element
	    private double latitude;
		@Element
	    private double longitude;
		@Element
	    private int messageID;
		@Element
	    private long time;
		@Element(required = false)
	    private DribSubject subject;
		@Element
	    private int likeCount;
		@Element
	    private int popularity;
		
	    public static final int maxLength = 144;

	    //Set the message constructor
	    public Drib()
	    {
	    }
	    
	    //Set the message constructor
	    public Drib(DribSubject subject, String text, double latitude, double longitude)
	    {
	        // Set fields directly (recommended by Google)
	    	//http://developer.android.com/guide/practices/design/performance.html
	    	messageID = 0;	
	    	likeCount = 0;
	    	popularity = 0;
	    	
	        this.subject = subject;
	        this.text = text;
	        this.latitude = latitude;
	        this.longitude = longitude;
	        
	        Log.i(TAG, "New Drib Created");

	    }
	    //Set and get the message text
	    public void setText(String text)
	    {
	        //Make sure the string length is not larger than 144 characters
	        if(text.length()>=maxLength)
	           text = text.substring(0,maxLength);
	        this.text = text;
	    }

	    public String getText()
	    {
	        return text;
	    }
	    //Set and get the latitude coordinates of the message
	    public void setLatitude(double latitude)
	    {
	        this.latitude = latitude;
	    }

	    public double getLatitude()
	    {
	        return latitude;
	    }
	    //Set and get the longitude coordinates of the message
	    public void setLongitude(double longitude)
	    {
	        this.longitude = longitude;
	    }

	    public double getLongitude()
	    {
	        return this.longitude;
	    }
	    //Set a Time object with current date and time
	    public void setCurrentTime(long date)
	    {
	        this.time = date;
	    }
	    public long getCurrentTime()
	    {
	        return this.time;
	    }
	    
	    //Set and get the subject for the message
	    public void setSubject(DribSubject subject)
	    {
	        this.subject = subject;
	    }
	    public DribSubject getSubject()
	    {
	        return this.subject;
	    }

	    //Set and get the message id
	    public void setMessageID(int messageID)
	    {
	        this.messageID = messageID;

	    }
	    public int getMessageID()
	    {
	        return this.messageID;

	    }
	     //Set and get the amount of users that like the message
	    public void setLikeCount(int like)
	    {
	        this.likeCount = like;
	    }

	     public int getLikeCount()
	    {
	        return likeCount;
	    }

	    //Set and get the popularity of the message
	    public void setPopularity(int popularity)
	    {
	        this.popularity = popularity;
	    }

	     public int getPopularity()
	    {
	        return popularity;
	    }
	}